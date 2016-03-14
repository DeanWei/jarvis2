/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:50:42
 */

package com.mogujie.jarvis.server.scheduler.task;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mogujie.jarvis.core.domain.JobContentType;
import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.core.domain.TaskDetail.TaskDetailBuilder;
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.core.domain.TaskType;
import com.mogujie.jarvis.core.exception.NotFoundException;
import com.mogujie.jarvis.core.util.IdUtils;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.dto.generate.Job;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.server.dispatcher.PriorityTaskQueue;
import com.mogujie.jarvis.server.dispatcher.TaskManager;
import com.mogujie.jarvis.server.domain.RetryType;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.scheduler.Scheduler;
import com.mogujie.jarvis.server.scheduler.TaskRetryScheduler;
import com.mogujie.jarvis.server.scheduler.event.AddTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.FailedEvent;
import com.mogujie.jarvis.server.scheduler.event.KilledEvent;
import com.mogujie.jarvis.server.scheduler.event.ManualRerunTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.RemoveTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.RetryTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.RunningEvent;
import com.mogujie.jarvis.server.scheduler.event.ScheduleEvent;
import com.mogujie.jarvis.server.scheduler.event.StartEvent;
import com.mogujie.jarvis.server.scheduler.event.StopEvent;
import com.mogujie.jarvis.server.scheduler.event.SuccessEvent;
import com.mogujie.jarvis.server.service.AppService;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.service.PlanService;
import com.mogujie.jarvis.server.service.ScriptService;
import com.mogujie.jarvis.server.service.TaskService;

/**
 * Scheduler used to handle ready tasks.
 *
 * @author guangming
 */
public class TaskScheduler extends Scheduler {
    private static TaskScheduler instance = new TaskScheduler();

    private TaskScheduler() {
    }

    public static TaskScheduler getInstance() {
        return instance;
    }

    private TaskGraph taskGraph = TaskGraph.INSTANCE;
    private AppService appService = Injectors.getInjector().getInstance(AppService.class);
    private JobService jobService = Injectors.getInjector().getInstance(JobService.class);
    private TaskService taskService = Injectors.getInjector().getInstance(TaskService.class);
    private PlanService planService = Injectors.getInjector().getInstance(PlanService.class);
    private ScriptService scriptService = Injectors.getInjector().getInstance(ScriptService.class);
    private TaskManager taskManager = Injectors.getInjector().getInstance(TaskManager.class);
    private PriorityTaskQueue taskQueue = Injectors.getInjector().getInstance(PriorityTaskQueue.class);
    private TaskRetryScheduler retryScheduler = TaskRetryScheduler.INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger();

    public void destroy() {
        taskGraph.clear();
    }

    @Override
    public void handleStartEvent(StartEvent event) {
    }

    @Override
    public void handleStopEvent(StopEvent event) {
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleSuccessEvent(SuccessEvent e) {
        long jobId = e.getJobId();
        long taskId = e.getTaskId();
        long scheduleTime = e.getScheduleTime();
        TaskType taskType = e.getTaskType();
        String reason = e.getReason();
        LOGGER.info("start handleSuccessEvent, taskId={}", taskId);

        // update success status
        taskService.updateStatusWithEnd(taskId, TaskStatus.SUCCESS, reason);
        LOGGER.info("update {} with SUCCESS status", taskId);

        // schedule child tasks
        scheduleChildTask(jobId, taskId, scheduleTime, taskType);

        // remove from taskGraph
        taskGraph.removeTask(taskId);
        LOGGER.info("remove {} from taskGraph", taskId);

        // reduce task number
        reduceTaskNum(taskId);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleRunningEvent(RunningEvent e) {
        long taskId = e.getTaskId();
        LOGGER.info("start handleRunningEvent, taskId={}", taskId);
        int workerId = e.getWorkerId();
        taskService.updateStatusWithStart(taskId, TaskStatus.RUNNING, workerId);
        LOGGER.info("update {} with RUNNING status", taskId);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleKilledEvent(KilledEvent e) {
        long taskId = e.getTaskId();
        LOGGER.info("start handleKilledEvent, taskId={}", taskId);
        taskService.updateStatusWithEnd(taskId, TaskStatus.KILLED);
        LOGGER.info("update {} with KILLED status", taskId);
        reduceTaskNum(taskId);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleRemoveTaskEvent(RemoveTaskEvent e) {
        long jobId = e.getJobId();
        long taskId = e.getTaskId();
        long scheduleTime = e.getScheduleTime();
        TaskType taskType = e.getTaskType();
        LOGGER.info("start handleRemoveTaskEvent, taskId={}", taskId);

        // 1. update taskService
        taskService.updateStatus(taskId, TaskStatus.REMOVED);

        // 2. schedule child tasks
        scheduleChildTask(jobId, taskId, scheduleTime, taskType);

        // 3. remove from taskGraph
        taskGraph.removeTask(taskId);

        // 4. remove from TaskQueue
        PriorityTaskQueue taskQueue = Injectors.getInjector().getInstance(PriorityTaskQueue.class);
        Task task = taskService.get(taskId);
        String fullId = IdUtils.getFullId(task.getJobId(), taskId, task.getAttemptId());
        taskQueue.removeByKey(fullId);

        // 5. remove from RetryScheduler
        String jobIdWithTaskId = fullId.replaceAll("_\\d+$", "");
        TaskRetryScheduler retryScheduler = TaskRetryScheduler.INSTANCE;
        retryScheduler.remove(jobIdWithTaskId, RetryType.FAILED_RETRY);
        retryScheduler.remove(jobIdWithTaskId, RetryType.REJECT_RETRY);

        // 6. reduce task number
        reduceTaskNum(taskId);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleFailedEvent(FailedEvent e) {
        long taskId = e.getTaskId();
        String reason = e.getReason();
        LOGGER.info("start handleFailedEvent, taskId={}", taskId);
        DAGTask dagTask = taskGraph.getTask(taskId);
        if (dagTask != null) {
            long jobId = dagTask.getJobId();
            Job job = jobService.get(jobId).getJob();
            int failedRetries = job.getFailedAttempts();

            int attemptId = dagTask.getAttemptId();
            LOGGER.info("attemptId={}, failedRetries={}", attemptId, failedRetries);

            TaskDetail taskDetail = null;
            try {
                taskDetail = getTaskInfo(dagTask);
            } catch (NotFoundException ex) {
                reason = ex.getMessage();
            }

            if (taskDetail != null && attemptId <= failedRetries) {
                taskService.insertHistory(taskId);
                LOGGER.info("insert task [taskId={},attemptId={}] to TaskHistory", taskId, attemptId);

                attemptId++;
                dagTask.setAttemptId(attemptId);
                Task task = new Task();
                task.setTaskId(taskId);
                task.setAttemptId(attemptId);
                task.setUpdateTime(DateTime.now().toDate());
                task.setStatus(TaskStatus.READY.getValue());
                taskService.updateSelective(task);
                LOGGER.info("update task {}, attemptId={}", taskId, attemptId);
                retryScheduler.addTask(taskDetail, RetryType.FAILED_RETRY);
                LOGGER.info("add to retryScheduler");
            } else {
                taskService.updateStatusWithEnd(taskId, TaskStatus.FAILED, reason);
                LOGGER.info("update {} with FAILED status", taskId);
                String key = jobId + "_" + taskId;
                retryScheduler.remove(key, RetryType.FAILED_RETRY);
                LOGGER.info("remove from retryScheduler, key={}", key);
            }
        }

        reduceTaskNum(taskId);
    }

    @Subscribe
    public void handleAddTaskEvent(AddTaskEvent e) {
        long jobId = e.getJobId();
        long scheduleTime = e.getScheduleTime();
        LOGGER.info("start handleAddTaskEvent, jobId={}, scheduleTime={}", jobId, new DateTime(scheduleTime));
        Map<Long, List<Long>> dependTaskIdMap = e.getDependTaskIdMap();

        //去重
        Task oldTask = taskService.getTaskByJobIdAndScheduleTime(jobId, scheduleTime);
        if (oldTask != null) {
            LOGGER.warn("jobId={}, scheduleTime={} has existed in Task table.", jobId, new DateTime(scheduleTime));
            return;
        }

        // create new task
        long taskId = taskService.createTaskByJobId(jobId, scheduleTime, scheduleTime, TaskType.SCHEDULE);
        LOGGER.info("add new task[{}] to DB", taskId);

        // 创建新的task的时候，重新刷新改job的plan
        DateTime now = DateTime.now();
        Range<DateTime> range = Range.closedOpen(now, now.plusDays(1).withTimeAtStartOfDay());
        planService.refreshPlan(jobId, range);

        // 如果是串行任务
        if (jobService.get(jobId).getJob().getIsSerial()) {
            // 首先检查自己上一次是否成功
            Task task = taskService.getLastTask(jobId, scheduleTime, TaskType.SCHEDULE);
            if (task != null) {
                if (task.getStatus() != TaskStatus.SUCCESS.getValue()) {
                    // 如果失败，标记为失败
                    String failedReason = "前置串行任务失败";
                    taskService.updateStatusWithEnd(taskId, TaskStatus.FAILED, failedReason);
                }
                // 增加自依赖
                List<Long> dependTaskIds = Lists.newArrayList(task.getTaskId());
                dependTaskIdMap.put(jobId, dependTaskIds);
            }
        }

        // add to taskGraph
        DAGTask dagTask = new DAGTask(jobId, taskId, scheduleTime, dependTaskIdMap);
        taskGraph.addTask(taskId, dagTask);
        LOGGER.info("add {} to taskGraph", dagTask);

        // add task dependency
        if (dependTaskIdMap != null) {
            for (Entry<Long, List<Long>> entry : dependTaskIdMap.entrySet()) {
                List<Long> preTaskIds = entry.getValue();
                for (long parentId : preTaskIds) {
                    taskGraph.addDependency(parentId, taskId);
                }
            }
        }

        // 如果通过依赖检查，提交给任务执行器
        if (dagTask.checkStatus()) {
            submitTask(dagTask);
        }
    }

    @Subscribe
    public void handleRetryTaskEvent(RetryTaskEvent e) {
        long taskId = e.getTaskId();
        LOGGER.info("start handleRetryTaskEvent, taskId={}", taskId);
        Task task = taskService.get(taskId);
        if (task != null) {
            task.setStatus(TaskStatus.WAITING.getValue());
            task.setUpdateTime(DateTime.now().toDate());
            task.setExecuteStartTime(null);
            task.setExecuteEndTime(null);
            taskService.update(task);
            LOGGER.info("update {} with WAITING status", taskId);

            DAGTask dagTask = taskGraph.getTask(taskId);
            if (dagTask == null) {
                dagTask = new DAGTask(task.getJobId(), taskId, task.getAttemptId(),
                        task.getScheduleTime().getTime(), task.getDataTime().getTime());
                taskGraph.addTask(taskId, dagTask);
                LOGGER.info("add {} to taskGraph", dagTask);
            }
            if (dagTask != null && dagTask.checkStatus()) {
                LOGGER.info("{} pass status check", dagTask);
                int attemptId = dagTask.getAttemptId();
                attemptId++;
                dagTask.setAttemptId(attemptId);
                Task updateTask = new Task();
                updateTask.setTaskId(taskId);
                updateTask.setAttemptId(attemptId);
                updateTask.setUpdateTime(DateTime.now().toDate());
                Job job = jobService.get(dagTask.getJobId()).getJob();
                String content = "";
                if (job.getContentType() == JobContentType.SCRIPT.getValue()) {
                    int scriptId = Integer.valueOf(job.getContent());
                    if (scriptService.getContentById(scriptId) != null ) {
                        content = scriptService.getContentById(scriptId);
                    } else {
                        LOGGER.error("cant't find content from scriptId={}", scriptId);
                    }
                } else {
                    content = job.getContent();
                }
                updateTask.setContent(content);
                updateTask.setExecuteUser(job.getUpdateUser());
                taskService.updateSelective(updateTask);
                LOGGER.info("update task {}, attemptId={}", taskId, attemptId);
                submitTask(dagTask);
            }
        }
    }

    @Subscribe
    public void handleManulRerunTaskEvent(ManualRerunTaskEvent e) {
        List<Long> taskIdList = e.getTaskIdList();
        LOGGER.info("start handleRetryTaskEvent, taskIdList={}", taskIdList);
        for (long taskId : taskIdList) {
            DAGTask dagTask = taskGraph.getTask(taskId);
            if (dagTask != null && dagTask.checkStatus()) {
                LOGGER.info("{} pass status check", dagTask);
                submitTask(dagTask);
            }
        }
    }

    @VisibleForTesting
    public PriorityTaskQueue getTaskQueue() {
        return taskQueue;
    }

    private void submitTask(DAGTask dagTask) {
        // update status to ready
        taskService.updateStatus(dagTask.getTaskId(), TaskStatus.READY);
        LOGGER.info("update {} with READY status", dagTask.getTaskId());

        // submit to TaskQueue
        try{
            TaskDetail taskDetail = getTaskInfo(dagTask);
            taskQueue.put(taskDetail);
        } catch (NotFoundException ex){
            LOGGER.error(ex);
        }
    }

    private void scheduleChildTask(long jobId, long taskId, long scheduleTime, TaskType taskType) {
        List<DAGTask> childTasks = taskGraph.getChildren(taskId);
        if (childTasks != null && !childTasks.isEmpty()) {
            // TaskGraph trigger
            // notify child tasks
            // notice: 如果是串行任务，之前失败了，这里也可以触发自身后续跑起来
            LOGGER.info("notify child tasks {}", childTasks);
            for (DAGTask childTask : childTasks) {
                if (childTask != null && childTask.checkStatus()) {
                    LOGGER.info("child {} pass the status check", childTask);
                    submitTask(childTask);
                }
            }
        }

        // 如果是正常调度，交给DAGScheduler触发后续任务
        if (taskType.equals(TaskType.SCHEDULE)) {
            // JobGraph trigger
            LOGGER.debug("[taskId={}, taskType=SCHEDULE], notify ScheduleEvent to DAGScheudler", taskId);
            ScheduleEvent event = new ScheduleEvent(jobId, taskId, scheduleTime);
            getSchedulerController().notify(event);
        }
    }

    private TaskDetail getTaskInfo(DAGTask dagTask) throws NotFoundException {
        String fullId = IdUtils.getFullId(dagTask.getJobId(), dagTask.getTaskId(), dagTask.getAttemptId());
        long jobId = dagTask.getJobId();
        Job job = jobService.get(jobId).getJob();
        TaskDetailBuilder builder = TaskDetail.newTaskDetailBuilder()
                .setFullId(fullId)
                .setTaskName(job.getJobName())
                .setAppName(appService.getAppNameByAppId(job.getAppId()))
                .setUser(job.getSubmitUser())
                .setPriority(job.getPriority())
                .setJobType(job.getJobType())
                .setParameters(JsonHelper.fromJson2JobParams(job.getParams()))
                .setScheduleTime(new DateTime(dagTask.getScheduleTime()))
                .setDataTime(new DateTime(dagTask.getDataTime()))
                .setGroupId(job.getWorkerGroupId())
                .setFailedRetries(job.getFailedAttempts())
                .setFailedInterval(job.getFailedInterval())
                .setExpiredTime(job.getExpiredTime());
        String content = "";
        if (job.getContentType() == JobContentType.SCRIPT.getValue()) {
            int scriptId = Integer.parseInt(job.getContent());
            if (scriptService.getContentById(scriptId) != null) {
                content = scriptService.getContentById(scriptId);
            } else {
                LOGGER.error("couldn't get content by scriptId={}", scriptId);
            }
        } else {
            content = job.getContent();
        }
        builder.setContent(content);
        return builder.build();
    }

    private void reduceTaskNum(long taskId) {
        Task task = taskService.get(taskId);
        if (task != null) {
            int appId = task.getAppId();
            taskManager.appCounterDecrement(appId);
        }
    }

}
