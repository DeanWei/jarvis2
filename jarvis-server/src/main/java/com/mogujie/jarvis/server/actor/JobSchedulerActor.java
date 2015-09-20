/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:35:46
 */

package com.mogujie.jarvis.server.actor;

import java.text.DateFormat;
import java.util.Date;
import java.util.Set;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.dao.JobDependMapper;
import com.mogujie.jarvis.dao.JobMapper;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.dto.JobDepend;
import com.mogujie.jarvis.protocol.ModifyJobFlagProtos.RestServerModifyJobFlagRequest;
import com.mogujie.jarvis.protocol.ModifyJobProtos.RestServerModifyJobRequest;
import com.mogujie.jarvis.protocol.ReportStatusProtos.WorkerReportStatusRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.RestServerSubmitJobRequest;
import com.mogujie.jarvis.server.observer.Event;
import com.mogujie.jarvis.server.observer.Observable;
import com.mogujie.jarvis.server.observer.Observer;
import com.mogujie.jarvis.server.scheduler.JobScheduleType;
import com.mogujie.jarvis.server.scheduler.SchedulerUtil;
import com.mogujie.jarvis.server.scheduler.dag.DAGScheduler;
import com.mogujie.jarvis.server.scheduler.dag.JobDependencyStrategy;
import com.mogujie.jarvis.server.scheduler.event.AddJobEvent;
import com.mogujie.jarvis.server.scheduler.event.FailedEvent;
import com.mogujie.jarvis.server.scheduler.event.InitEvent;
import com.mogujie.jarvis.server.scheduler.event.KilledEvent;
import com.mogujie.jarvis.server.scheduler.event.ModifyJobEvent;
import com.mogujie.jarvis.server.scheduler.event.ModifyJobEvent.MODIFY_TYPE;
import com.mogujie.jarvis.server.scheduler.event.ModifyJobFlagEvent;
import com.mogujie.jarvis.server.scheduler.event.RunningEvent;
import com.mogujie.jarvis.server.scheduler.event.StopEvent;
import com.mogujie.jarvis.server.scheduler.event.SuccessEvent;
import com.mogujie.jarvis.server.scheduler.event.UnhandleEvent;
import com.mogujie.jarvis.server.scheduler.task.TaskScheduler;
import com.mogujie.jarvis.server.scheduler.time.TimeScheduler;
import com.mogujie.jarvis.server.service.CrontabService;
import com.mogujie.jarvis.server.service.JobDependService;

import akka.actor.UntypedActor;

/**
 * Actor used to schedule job with three schedulers (
 * {@link com.mogujie.jarvis.server.scheduler.time.TimeScheduler},
 * {@link com.mogujie.jarvis.server.scheduler.dag.DAGScheduler}, and
 * {@link com.mogujie.jarvis.server.scheduler.task.TaskScheduler})
 *
 * @author guangming
 *
 */
@Named("JobSchedulerActor")
@Scope("prototype")
public class JobSchedulerActor extends UntypedActor implements Observable {
    @Autowired
    private TimeScheduler timeScheduler;

    @Autowired
    private DAGScheduler dagScheduler;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private CrontabService cronService;

    @Autowired
    private JobDependService jobDependService;

    @Autowired
    JobMapper jobMapper;

    @Autowired
    JobDependMapper jobDependMapper;

    private EventBus eventBus = new EventBus("JobSchedulerActor");

    @Override
    public void preStart() throws Exception {
        register(timeScheduler);
        register(dagScheduler);
        register(taskScheduler);

        notify(new InitEvent());
    }

    @Override
    public void preRestart(Throwable reason, scala.Option<Object> message) throws Exception {

    }

    @Override
    public void onReceive(Object obj) throws Exception {
        Event event = new UnhandleEvent();
        if (obj instanceof WorkerReportStatusRequest) {
            WorkerReportStatusRequest msg = (WorkerReportStatusRequest) obj;
            String fullId = msg.getFullId();
            String[] idList = fullId.split("_");
            long jobId = Long.valueOf(idList[0]);
            long taskId = Long.valueOf(idList[1]);

            JobStatus status = JobStatus.getInstance(msg.getStatus());
            if (status.equals(JobStatus.SUCCESS)) {
                event = new SuccessEvent(jobId, taskId);
            } else if (status.equals(JobStatus.FAILED)) {
                event = new FailedEvent(jobId, taskId);
            } else if (status.equals(JobStatus.RUNNING)) {
                event = new RunningEvent(jobId, taskId);
            } else if (status.equals(JobStatus.KILLED)) {
                event = new KilledEvent(jobId, taskId);
            }
        } else if (obj instanceof RestServerSubmitJobRequest) {
            RestServerSubmitJobRequest msg = (RestServerSubmitJobRequest) obj;
            // 1. insert job to DB
            Job job = SchedulerUtil.convert2Job(msg);
            jobMapper.insert(job);
            long jobId = job.getJobId();
            // 如果是新增任务（不是手动触发），则originId=jobId
            if (job.getOriginJobId() == null) {
                job.setOriginJobId(jobId);
                jobMapper.updateByPrimaryKey(job);
            }
            Set<Long> needDependencies = Sets.newHashSet();
            if (msg.getDependencyJobidsList() != null) {
                needDependencies.addAll(msg.getDependencyJobidsList());
            }
            // 2. insert cron to DB
            cronService.insert(jobId, msg.getCronExpression());
            // 3. insert jobDepend to DB
            for (long d : needDependencies) {
                JobDepend jobDepend = new JobDepend();
                jobDepend.setJobId(jobId);
                jobDepend.setPreJobId(d);
                Date currentTime = new Date();
                DateFormat dateTimeFormat = DateFormat.getDateTimeInstance();
                dateTimeFormat.format(currentTime);
                jobDepend.setCreateTime(currentTime);
                jobDepend.setUpdateTime(currentTime);
                jobDepend.setUpdateUser(msg.getUser());
                jobDependMapper.insert(jobDepend);
            }
            // 4. get jobScheduleType
            boolean hasCron = (msg.getCronExpression() != null);
            boolean hasDepend = (!needDependencies.isEmpty());
            JobScheduleType type = SchedulerUtil.getJobScheduleType(hasCron, hasDepend);
            // 5. get JobDependencyStrategy
            JobDependencyStrategy strategy = JobDependencyStrategy.ALL;
            event = new AddJobEvent(jobId, needDependencies, type, strategy);
        } else if (obj instanceof RestServerModifyJobRequest) {
            RestServerModifyJobRequest msg = (RestServerModifyJobRequest) obj;
            long jobId = msg.getJobId();
            // 1. update job to DB
            Job job = SchedulerUtil.convert2Job(msg);
            jobMapper.updateByPrimaryKey(job);
            // 2. update cron to DB
            cronService.update(jobId, msg.getCronExpression());
            // 3. update jobDepend to DB
            Set<Long> needDependencies = Sets.newHashSet();
            if (msg.getDependencyJobidsList() != null) {
                needDependencies.addAll(msg.getDependencyJobidsList());
            }
            jobDependService.deleteByJobId(jobId);
            for (long d : needDependencies) {
                JobDepend jobDepend = new JobDepend();
                jobDepend.setJobId(jobId);
                jobDepend.setPreJobId(d);
                Date currentTime = new Date();
                DateFormat dateTimeFormat = DateFormat.getDateTimeInstance();
                dateTimeFormat.format(currentTime);
                jobDepend.setUpdateTime(currentTime);
                jobDepend.setUpdateUser(msg.getUser());
                jobDependMapper.insert(jobDepend);
            }
            boolean hasCron = (msg.getCronExpression() != null);
            event = new ModifyJobEvent(jobId, needDependencies, MODIFY_TYPE.MODIFY, hasCron);
        } else if (obj instanceof RestServerModifyJobFlagRequest) {
            RestServerModifyJobFlagRequest msg = (RestServerModifyJobFlagRequest) obj;
            long jobId = msg.getJobId();
            event = new ModifyJobFlagEvent(jobId, JobFlag.DELETED);
        } else {
            unhandled(obj);
        }

        notify(event);
    }

    @Override
    public void postStop() throws Exception {
        notify(new StopEvent());

        eventBus.unregister(timeScheduler);
        eventBus.unregister(dagScheduler);
        eventBus.unregister(taskScheduler);
    }

    @Override
    public void register(Observer o) {
        eventBus.register(o);
    }

    @Override
    public void unregister(Observer o) {
        eventBus.unregister(o);
    }

    @Override
    public void notify(Event event) {
        eventBus.post(event);
    }

}
