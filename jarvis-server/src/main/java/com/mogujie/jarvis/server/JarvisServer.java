/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月21日 下午4:11:14
 */

package com.mogujie.jarvis.server;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.Lists;
import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.core.exeception.JobScheduleException;
import com.mogujie.jarvis.core.expression.CronExpression;
import com.mogujie.jarvis.core.expression.FixedDelayExpression;
import com.mogujie.jarvis.core.expression.FixedRateExpression;
import com.mogujie.jarvis.core.expression.ISO8601Expression;
import com.mogujie.jarvis.core.expression.ScheduleExpression;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.dto.generate.Job;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.server.alarm.AlarmScheduler;
import com.mogujie.jarvis.server.domain.JobEntry;
import com.mogujie.jarvis.server.scheduler.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.TaskRetryScheduler;
import com.mogujie.jarvis.server.scheduler.dag.DAGJob;
import com.mogujie.jarvis.server.scheduler.dag.DAGJobType;
import com.mogujie.jarvis.server.scheduler.dag.DAGScheduler;
import com.mogujie.jarvis.server.scheduler.dag.JobGraph;
import com.mogujie.jarvis.server.scheduler.event.StartEvent;
import com.mogujie.jarvis.server.scheduler.plan.ExecutionPlan;
import com.mogujie.jarvis.server.scheduler.plan.ExecutionPlanEntry;
import com.mogujie.jarvis.server.scheduler.task.DAGTask;
import com.mogujie.jarvis.server.scheduler.task.TaskGraph;
import com.mogujie.jarvis.server.scheduler.task.TaskScheduler;
import com.mogujie.jarvis.server.scheduler.time.TimeScheduler;
import com.mogujie.jarvis.server.scheduler.time.TimeSchedulerFactory;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.service.TaskService;
import com.mogujie.jarvis.server.util.SpringContext;
import com.mogujie.jarvis.server.util.SpringExtension;

import akka.actor.ActorSystem;
import akka.routing.RoundRobinPool;

public class JarvisServer {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) throws Exception {
        LOGGER.info("Starting Jarvis server...");

        ApplicationContext context = SpringContext.getApplicationContext();
        ActorSystem system = JarvisServerActorSystem.getInstance();
        SpringExtension.SPRING_EXT_PROVIDER.get(system).initialize(context);

        Configuration config = ConfigUtils.getServerConfig();
        int serverActorNum = config.getInt(ServerConigKeys.SERVER_ACTOR_NUM, 500);
        system.actorOf(SpringExtension.SPRING_EXT_PROVIDER.get(system).props("serverActor").withRouter(new RoundRobinPool(serverActorNum)),
                JarvisConstants.SERVER_AKKA_SYSTEM_NAME);

        int taskDispatcherThreads = config.getInt(ServerConigKeys.SERVER_DISPATCHER_THREADS, 5);
        ExecutorService executorService = Executors.newFixedThreadPool(taskDispatcherThreads);
        for (int i = 0; i < taskDispatcherThreads; i++) {
            executorService.submit(SpringContext.getBean(TaskDispatcher.class));
        }
        executorService.shutdown();

        TaskRetryScheduler taskRetryScheduler = TaskRetryScheduler.INSTANCE;
        taskRetryScheduler.start();

        init();

        LOGGER.info("Jarvis server started.");
    }

    public static void init() throws Exception {
        initScheduler();
        // initTimerTask();
    }

    private static void initScheduler() throws JobScheduleException, CycleFoundException {
        // 1. register schedulers to controller
        JobSchedulerController controller = JobSchedulerController.getInstance();
        DAGScheduler dagScheduler = DAGScheduler.getInstance();
        TaskScheduler taskScheduler = TaskScheduler.getInstance();
        TimeScheduler timeScheduler = TimeSchedulerFactory.getInstance();
        AlarmScheduler alarmScheduler = SpringContext.getBean(AlarmScheduler.class);
        JobGraph jobGraph = JobGraph.INSTANCE;
        TaskGraph taskGraph = TaskGraph.INSTANCE;
        controller.register(dagScheduler);
        controller.register(taskScheduler);
        controller.register(timeScheduler);
        controller.register(alarmScheduler);

        // 2. initialize DAGScheduler and TimeScheduler
        JobService jobService = SpringContext.getBean(JobService.class);
        TaskService taskService = SpringContext.getBean(TaskService.class);
        List<Job> jobs = jobService.getNotDeletedJobs();
        // 2.1 先添加job
        for (Job job : jobs) {
            long jobId = job.getJobId();
            JobEntry jobEntry = jobService.get(jobId);
            Set<Long> dependencies = jobEntry.getDependencies().keySet();
            int cycleFlag = 0;
            int timeFlag = 0;
            List<ScheduleExpression> timeExpressions = jobEntry.getScheduleExpressions();
            if (!timeExpressions.isEmpty()) {
                for (ScheduleExpression expression : timeExpressions) {
                    if (expression instanceof CronExpression || expression instanceof FixedRateExpression
                            || expression instanceof ISO8601Expression) {
                        timeFlag = 1;
                    } else if (expression instanceof FixedDelayExpression) {
                        cycleFlag = 1;
                    }
                }
            }
            int dependFlag = (!dependencies.isEmpty()) ? 1 : 0;
            DAGJobType type = DAGJobType.getDAGJobType(timeFlag, dependFlag, cycleFlag);
            JobFlag flag = JobFlag.getInstance(job.getJobFlag());
            dagScheduler.getJobGraph().addJob(jobId, new DAGJob(jobId, type, flag), null);
            if (type.implies(DAGJobType.TIME) && flag.equals(JobFlag.ENABLE) && jobService.isActive(jobId)) {
                timeScheduler.addJob(jobId);
            }
        }
        // 2.2 再添加依赖关系
        for (Job job : jobs) {
            long jobId = job.getJobId();
            JobEntry jobEntry = jobService.get(jobId);
            Set<Long> dependencies = jobEntry.getDependencies().keySet();
            for (long parentId : dependencies) {
                dagScheduler.getJobGraph().addDependency(parentId, jobId);
            }
        }

        // 3. initialize TaskScheduler
        List<Task> recoveryTasks = taskService
                .getTasksByStatusNotIn(Lists.newArrayList(TaskStatus.SUCCESS.getValue(), TaskStatus.REMOVED.getValue()));
        // 3.1 先恢复task
        for (Task task : recoveryTasks) {
            DAGTask dagTask = new DAGTask(task.getJobId(), task.getTaskId(), task.getAttemptId(), task.getScheduleTime().getTime());
            taskGraph.addTask(task.getTaskId(), dagTask);
        }
        // 3.2 再构造task依赖关系
        for (Task task : recoveryTasks) {
            DAGTask dagTask = taskGraph.getTask(task.getTaskId());
            List<Long> dependTaskIds = dagTask.getDependTaskIds();
            if (dependTaskIds != null) {
                for (Long parentId : dependTaskIds) {
                    taskGraph.addDependency(parentId, task.getTaskId());
                }
            }
        }
        // 3.3 把waiting和ready的task重新加入执行计划
        List<Task> readyTasks = taskService.getTasksByStatus(Lists.newArrayList(TaskStatus.WAITING.getValue(), TaskStatus.READY.getValue()));
        for (Task task : readyTasks) {
            long jobId = task.getJobId();
            DAGJob dagJob = jobGraph.getDAGJob(jobId);
            if (dagJob.getType().implies(DAGJobType.TIME)) {
                ExecutionPlan plan = ExecutionPlan.INSTANCE;
                ExecutionPlanEntry planEntry = new ExecutionPlanEntry(jobId, new DateTime(task.getScheduleTime()), task.getTaskId());
                plan.addPlan(planEntry);
            }
        }

        // 4. start schedulers
        controller.notify(new StartEvent());
    }
}
