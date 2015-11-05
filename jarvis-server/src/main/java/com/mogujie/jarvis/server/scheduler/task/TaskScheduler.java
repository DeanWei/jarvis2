/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:50:42
 */

package com.mogujie.jarvis.server.scheduler.task;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.core.util.ThreadUtils;
import com.mogujie.jarvis.dao.AppMapper;
import com.mogujie.jarvis.dao.JobMapper;
import com.mogujie.jarvis.dao.TaskMapper;
import com.mogujie.jarvis.dto.App;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.dto.Task;
import com.mogujie.jarvis.server.TaskManager;
import com.mogujie.jarvis.server.TaskQueue;
import com.mogujie.jarvis.server.scheduler.Scheduler;
import com.mogujie.jarvis.server.scheduler.SchedulerUtil;
import com.mogujie.jarvis.server.scheduler.event.AddTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.FailedEvent;
import com.mogujie.jarvis.server.scheduler.event.KilledEvent;
import com.mogujie.jarvis.server.scheduler.event.RunningEvent;
import com.mogujie.jarvis.server.scheduler.event.ScheduleEvent;
import com.mogujie.jarvis.server.scheduler.event.StartEvent;
import com.mogujie.jarvis.server.scheduler.event.StopEvent;
import com.mogujie.jarvis.server.scheduler.event.SuccessEvent;
import com.mogujie.jarvis.server.service.TaskDependService;
import com.mogujie.jarvis.server.service.TaskService;

/**
 * Scheduler used to handle ready tasks.
 *
 * @author guangming
 *
 */
@Repository
public class TaskScheduler extends Scheduler {
    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private AppMapper appMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskDependService taskDependService;

    @Autowired
    private TaskManager taskManager;

    @Autowired
    private TaskQueue taskQueue;

    private Map<Long, DAGTask> readyTable = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LogManager.getLogger();

    private PriorityBlockingQueue<FailedTask> failedQueue = new PriorityBlockingQueue<>(10,
            new Comparator<FailedTask>() {
        @Override
        public int compare(FailedTask t1, FailedTask t2) {
            return (int)(t1.getNextStartTime() - t2.getNextStartTime());
        }
    });

    class FailedScanThread extends Thread {
        public FailedScanThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (true) {
                while (!failedQueue.isEmpty()) {
                    FailedTask failedTask = failedQueue.peek();
                    long currentTime = System.currentTimeMillis();
                    if (failedTask.getNextStartTime() <= currentTime) {
                        retryTask(failedTask.getTask());
                        failedQueue.poll();
                    } else {
                        break;
                    }
                }
                ThreadUtils.sleep(1000);
            }
        }
    }
    private FailedScanThread scanThread;

    private boolean isTestMode = SchedulerUtil.isTestMode();

    private static final int DEFAULT_MAX_FAILED_ATTEMPTS = 3;
    private static final int DEFAULT_FAILED_INTERVAL = 1000;

    @Override
    @Transactional
    protected void init() {
        getSchedulerController().register(this);

        // load all READY tasks from DB
        List<Task> readyTasks = taskService.getTasksByStatus(JobStatus.READY);
        if (readyTasks != null) {
            for (Task task : readyTasks) {
                DAGTask dagTask = new DAGTask(task.getJobId(), task.getTaskId(), task.getAttemptId());
                readyTable.put(task.getTaskId(), dagTask);
                retryTask(task);
            }
        }

        // load all RUNNING tasks from DB
        List<Task> runningTasks = taskService.getTasksByStatus(JobStatus.RUNNING);
        if (runningTasks != null) {
            for (Task task : runningTasks) {
                DAGTask dagTask = new DAGTask(task.getJobId(), task.getTaskId(), task.getAttemptId());
                readyTable.put(task.getTaskId(), dagTask);
            }
        }

        scanThread = new FailedScanThread("FailedScanThread");
        scanThread.start();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void destroy() {
        clear();
        getSchedulerController().unregister(this);
        scanThread.stop();
    }

    @Override
    public void handleStartEvent(StartEvent event) {
    }

    @Override
    public void handleStopEvent(StopEvent event) {
    }

    @Subscribe
    @AllowConcurrentEvents
    @Transactional
    public void handleSuccessEvent(SuccessEvent e) {
        long taskId = e.getTaskId();
        // update task status and remove from readyTable
        updateTaskStatus(taskId, JobStatus.SUCCESS);
        readyTable.remove(taskId);

        // notify child tasks
        List<Long> childTasks = taskDependService.getChildTaskIds(taskId);
        for (Long childId : childTasks) {
            DAGTask dagTask = readyTable.get(childId);
            if (dagTask != null && dagTask.checkStatus()) {
                LOGGER.debug("DAGTask {} pass the status check when handle SuccessEvent", dagTask.getTaskId());
                submitTask(dagTask);
            }
        }

        // reduce task num
        reduceTaskNum(e.getJobId());
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleRunningEvent(RunningEvent e) {
        long taskId = e.getTaskId();
        updateTaskStatus(taskId, JobStatus.RUNNING);
    }

    @Subscribe
    @AllowConcurrentEvents
    @Transactional
    public void handleKilledEvent(KilledEvent e) {
        long taskId = e.getTaskId();
        updateTaskStatus(taskId, JobStatus.KILLED);
        readyTable.remove(taskId);
        reduceTaskNum(e.getJobId());
    }

    @Subscribe
    @AllowConcurrentEvents
    @Transactional
    public void handleFailedEvent(FailedEvent e) {
        DAGTask dagTask = readyTable.get(e.getTaskId());
        long taskId = e.getTaskId();
        if (dagTask != null) {

            int maxFailedAttempts = DEFAULT_MAX_FAILED_ATTEMPTS;
            int failedInterval = DEFAULT_FAILED_INTERVAL;
            if (!isTestMode) {
                Job job = jobMapper.selectByPrimaryKey(dagTask.getJobId());
                maxFailedAttempts = job.getFailedAttempts();
                failedInterval = job.getFailedInterval();
            }

            if (dagTask.getAttemptId() <= maxFailedAttempts) {
                int attemptId = dagTask.getAttemptId();
                attemptId++;
                dagTask.setAttemptId(attemptId);
                long currentTime = System.currentTimeMillis();
                long nextStartTime = (currentTime / 1000 + failedInterval) * 1000;
                if (!isTestMode) {
                    Task task = taskMapper.selectByPrimaryKey(taskId);
                    if (task != null) {
                        failedQueue.put(new FailedTask(task, nextStartTime));
                    }
                }
            } else {
                updateTaskStatus(e.getTaskId(), JobStatus.FAILED);
                readyTable.remove(taskId);
            }
        }

        reduceTaskNum(e.getJobId());
    }

    @Subscribe
    public void handleAddTaskEvent(AddTaskEvent e) {
        long jobId = e.getJobId();
        long taskId = e.getTaskId();
        long scheduleTime = e.getScheduleTime();

        // add to readyTable
        DAGTask dagTask = new DAGTask(jobId, taskId, scheduleTime);
        if (!readyTable.containsKey(taskId)) {
            readyTable.put(taskId, dagTask);

            if (dagTask.checkStatus()) {
                LOGGER.debug("DAGTask {} pass the status check when handle AddTaskEvent", dagTask.getTaskId());
                submitTask(dagTask);
            }
        }
    }

    private void updateTaskStatus(long taskId, JobStatus status) {
        if (!isTestMode) {
            if (status.equals(JobStatus.RUNNING)) {
                taskService.updateStatusWithStart(taskId, status);
            } else if (status.getValue() == JobStatus.SUCCESS.getValue() || status.getValue() == JobStatus.FAILED.getValue()
                    || status.getValue() == JobStatus.KILLED.getValue()) {
                taskService.updateStatusWithEnd(taskId, status);
            }
        }
    }

    @VisibleForTesting
    public void clear() {
        readyTable.clear();
    }

    @VisibleForTesting
    public Map<Long, DAGTask> getReadyTable() {
        return readyTable;
    }

    public void retryTask(long taskId) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        DAGTask dagTask = readyTable.get(taskId);
        if (task != null && dagTask != null) {
            int attemptId = dagTask.getAttemptId();
            attemptId++;
            dagTask.setAttemptId(attemptId);
            retryTask(task);
        }
    }

    private void retryTask(Task task) {
        long taskId = task.getTaskId();
        DAGTask dagTask = readyTable.get(taskId);
        if (dagTask != null) {
            if (!isTestMode) {
                task.setAttemptId(dagTask.getAttemptId());
                task.setUpdateTime(DateTime.now().toDate());
                taskMapper.updateByPrimaryKey(task);
            }

            submitTask(dagTask);
        }
    }

    private void submitTask(DAGTask dagTask) {
        // submit to TaskQueue
        TaskDetail taskDetail = getTaskInfo(dagTask);
        if (taskDetail != null) {
            taskQueue.put(taskDetail);
        }

        // send ScheduleEvent
        ScheduleEvent event = new ScheduleEvent(dagTask.getJobId(), dagTask.getTaskId(),
                dagTask.getScheduleTime());
        getSchedulerController().notify(event);
    }

    public void submitTask(long taskId) {
        DAGTask dagTask = readyTable.get(taskId);
        if (dagTask != null) {
            submitTask(dagTask);
        }
    }

    private TaskDetail getTaskInfo(DAGTask dagTask) {
        String fullId = dagTask.getJobId() + "_" + dagTask.getTaskId() + "_" + dagTask.getAttemptId();
        TaskDetail taskDetail = null;
        if (!isTestMode) {
            Job job = jobMapper.selectByPrimaryKey(dagTask.getJobId());
            App app = appMapper.selectByPrimaryKey(job.getAppId());
            taskDetail = TaskDetail.newTaskDetailBuilder()
                    .setFullId(fullId)
                    .setTaskName(job.getJobName())
                    .setAppName(app.getAppName())
                    .setUser(job.getSubmitUser())
                    .setPriority(job.getPriority())
                    .setContent(job.getContent())
                    .setTaskType(job.getJobType())
                    .setParameters(JsonHelper.parseJSON2Map(job.getParams()))
                    .build();
        }

        return taskDetail;
    }

    private void reduceTaskNum(long jobId) {
        if (!isTestMode) {
            Job job = jobMapper.selectByPrimaryKey(jobId);
            taskManager.appCounterDecrement(job.getAppId());
        }
    }
}
