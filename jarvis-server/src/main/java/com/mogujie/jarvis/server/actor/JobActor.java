/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月23日 上午10:15:14
 */

package com.mogujie.jarvis.server.actor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;

import akka.actor.UntypedActor;

import com.google.common.collect.Sets;
import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.dao.JobDependMapper;
import com.mogujie.jarvis.dao.JobMapper;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.dto.JobDepend;
import com.mogujie.jarvis.dto.JobDependExample;
import com.mogujie.jarvis.dto.JobDependKey;
import com.mogujie.jarvis.protocol.DependencyEntryProtos.DependencyEntry;
import com.mogujie.jarvis.protocol.DependencyEntryProtos.DependencyEntry.DependencyOperator;
import com.mogujie.jarvis.protocol.ModifyDependencyProtos.RestServerModifyDependencyRequest;
import com.mogujie.jarvis.protocol.ModifyDependencyProtos.ServerModifyDependencyResponse;
import com.mogujie.jarvis.protocol.ModifyJobFlagProtos.RestServerModifyJobFlagRequest;
import com.mogujie.jarvis.protocol.ModifyJobFlagProtos.ServerModifyJobFlagResponse;
import com.mogujie.jarvis.protocol.ModifyJobProtos.RestServerModifyJobRequest;
import com.mogujie.jarvis.protocol.ModifyJobProtos.ServerModifyJobResponse;
import com.mogujie.jarvis.protocol.QueryJobRelationProtos.JobFlagEntry;
import com.mogujie.jarvis.protocol.QueryJobRelationProtos.RestServerQueryJobRelationRequest;
import com.mogujie.jarvis.protocol.QueryJobRelationProtos.RestServerQueryJobRelationRequest.RelationType;
import com.mogujie.jarvis.protocol.QueryJobRelationProtos.ServerQueryJobRelationResponse;
import com.mogujie.jarvis.protocol.SubmitJobProtos.RestServerSubmitJobRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.ServerSubmitJobResponse;
import com.mogujie.jarvis.server.domain.ModifyDependEntry;
import com.mogujie.jarvis.server.domain.ModifyJobEntry;
import com.mogujie.jarvis.server.domain.ModifyJobType;
import com.mogujie.jarvis.server.domain.ModifyOperation;
import com.mogujie.jarvis.server.domain.RemoveJobRequest;
import com.mogujie.jarvis.server.scheduler.SchedulerUtil;
import com.mogujie.jarvis.server.scheduler.dag.DAGJob;
import com.mogujie.jarvis.server.scheduler.dag.DAGJobType;
import com.mogujie.jarvis.server.scheduler.dag.DAGScheduler;
import com.mogujie.jarvis.server.scheduler.time.TimeScheduler;
import com.mogujie.jarvis.server.service.AppService;
import com.mogujie.jarvis.server.service.CrontabService;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.util.MessageUtil;

/**
 * @author guangming
 *
 */
@Named("jobActor")
@Scope("prototype")
public class JobActor extends UntypedActor {

    @Autowired
    private DAGScheduler dagScheduler;

    @Autowired
    private TimeScheduler timeScheduler;

    @Autowired
    private CrontabService cronService;

    @Autowired
    private JobService jobService;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private AppService appService;

    @Autowired
    private JobDependMapper jobDependMapper;

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof RestServerSubmitJobRequest) {
            RestServerSubmitJobRequest msg = (RestServerSubmitJobRequest) obj;
            submitJob(msg);
        } else if (obj instanceof RestServerModifyJobRequest) {
            RestServerModifyJobRequest msg = (RestServerModifyJobRequest) obj;
            modifyJob(msg);
        } else if (obj instanceof RestServerModifyDependencyRequest) {
            RestServerModifyDependencyRequest msg = (RestServerModifyDependencyRequest) obj;
            modifyDependency(msg);
        } else if (obj instanceof RestServerModifyJobFlagRequest) {
            RestServerModifyJobFlagRequest msg = (RestServerModifyJobFlagRequest) obj;
            modifyJobFlag(msg);
        } else if (obj instanceof RemoveJobRequest) {
            RemoveJobRequest msg = (RemoveJobRequest) obj;
            removeJob(msg);
        } else if (obj instanceof RestServerQueryJobRelationRequest) {
            RestServerQueryJobRelationRequest msg = (RestServerQueryJobRelationRequest) obj;
            queryJobRelation(msg);
        } else {
            unhandled(obj);
        }
    }

    @Transactional
    private void submitJob(RestServerSubmitJobRequest msg) throws IOException {
        Set<Long> needDependencies = Sets.newHashSet();
        // 1. insert job to DB
        Job job = MessageUtil.convert2Job(appService, msg);
        jobMapper.insert(job);
        long jobId = job.getJobId();
        // 如果是新增任务（不是手动触发），则originId=jobId
        if (job.getOriginJobId() == null || job.getOriginJobId() == 0) {
            job.setOriginJobId(jobId);
            jobMapper.updateByPrimaryKey(job);
        }

        // 2. insert cron to DB
        cronService.insert(jobId, msg.getCronExpression());

        // 3. insert jobDepend to DB
        for (DependencyEntry entry : msg.getDependencyEntryList()) {
            needDependencies.add(entry.getJobId());
            JobDepend jobDepend = MessageUtil.convert2JobDepend(jobId, entry.getJobId(), entry.getCommonDependStrategy(),
                    entry.getOffsetDependStrategy(), msg.getUser());
            jobDependMapper.insert(jobDepend);
        }

        // 4. add job to scheduler
        int cycleFlag = msg.hasFixedDelay() ? 1 : 0;
        int dependFlag = (!needDependencies.isEmpty()) ? 1 : 0;
        int timeFlag = msg.hasCronExpression() ? 1 : 0;
        DAGJobType type = SchedulerUtil.getDAGJobType(cycleFlag, dependFlag, timeFlag);
        ServerSubmitJobResponse response;
        try {
            dagScheduler.addJob(jobId, new DAGJob(jobId, type), needDependencies);
            timeScheduler.addJob(jobId);
            response = ServerSubmitJobResponse.newBuilder()
                    .setSuccess(true)
                    .setJobId(jobId)
                    .build();
            getSender().tell(response, getSelf());
        } catch (Exception e) {
            response = ServerSubmitJobResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(e.getMessage())
                    .build();
            getSender().tell(response, getSelf());
            throw new IOException(e);
        }
    }

    @Transactional
    private void modifyJob(RestServerModifyJobRequest msg) throws IOException {
        long jobId = msg.getJobId();
        // 1. update job to DB
        Job job = MessageUtil.convert2Job(jobMapper, appService, msg);
        jobMapper.updateByPrimaryKey(job);

        // 2. update cron to DB
        if (msg.hasCronExpression()) {
            cronService.updateOrDelete(jobId, msg.getCronExpression());
        }

        // 3. scheduler modify job
        Map<ModifyJobType, ModifyJobEntry> modifyMap = MessageUtil.convert2ModifyJobMap(msg, jobService, cronService);
        ServerModifyJobResponse response;
        try {
            dagScheduler.modifyDAGJobType(jobId, modifyMap);
            timeScheduler.modifyJob(jobId);
            response = ServerModifyJobResponse.newBuilder()
                    .setSuccess(true)
                    .build();
            getSender().tell(response, getSelf());
        } catch (Exception e) {
            response = ServerModifyJobResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(e.getMessage())
                    .build();
            getSender().tell(response, getSelf());
            throw new IOException(e);
        }
    }

    @Transactional
    private void modifyDependency(RestServerModifyDependencyRequest msg) throws IOException {
        long jobId = msg.getJobId();
        List<ModifyDependEntry> dependEntries = new ArrayList<ModifyDependEntry>();
        for (DependencyEntry entry : msg.getDependencyEntryList()) {
            long preJobId = entry.getJobId();
            int commonStrategyValue = entry.getCommonDependStrategy();
            String offsetStrategyValue = entry.getOffsetDependStrategy();
            String user = msg.getUser();

            ModifyOperation operation;
            if (entry.getOperator().equals(DependencyOperator.ADD)) {
                operation = ModifyOperation.ADD;
                JobDepend jobDepend = MessageUtil.convert2JobDepend(jobId, preJobId, entry.getCommonDependStrategy(),
                        entry.getOffsetDependStrategy(), user);
                jobDependMapper.insert(jobDepend);
            } else if (entry.getOperator().equals(DependencyOperator.REMOVE)) {
                operation = ModifyOperation.DEL;
                JobDependKey key = new JobDependKey();
                key.setJobId(jobId);
                key.setPreJobId(preJobId);
                jobDependMapper.deleteByPrimaryKey(key);
            } else {
                operation = ModifyOperation.MODIFY;
                JobDependKey key = new JobDependKey();
                key.setJobId(jobId);
                key.setPreJobId(preJobId);
                JobDepend record = jobDependMapper.selectByPrimaryKey(key);
                if (record != null) {
                    record.setCommonStrategy(commonStrategyValue);
                    record.setOffsetStrategy(offsetStrategyValue);
                    record.setUpdateUser(user);
                    DateTime dt = DateTime.now();
                    Date currentTime = dt.toDate();
                    record.setUpdateTime(currentTime);
                    jobDependMapper.updateByPrimaryKey(record);
                }
            }
            ModifyDependEntry dependEntry = new ModifyDependEntry(operation, preJobId, commonStrategyValue, offsetStrategyValue);
            dependEntries.add(dependEntry);
        }
        ServerModifyDependencyResponse response;
        try {
            dagScheduler.modifyDependency(jobId, dependEntries);
            response = ServerModifyDependencyResponse.newBuilder()
                    .setSuccess(true)
                    .build();
            getSender().tell(response, getSelf());
        } catch (Exception e) {
            response = ServerModifyDependencyResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(e.getMessage())
                    .build();
            getSender().tell(response, getSelf());
            throw new IOException(e);
        }
    }

    @Transactional
    private void modifyJobFlag(RestServerModifyJobFlagRequest msg) throws IOException {
        long jobId = msg.getJobId();
        Job record = jobMapper.selectByPrimaryKey(jobId);
        record.setJobFlag(msg.getJobFlag());
        record.setUpdateUser(msg.getUser());
        DateTime dt = DateTime.now();
        Date currentTime = dt.toDate();
        record.setUpdateTime(currentTime);
        jobMapper.updateByPrimaryKey(record);
        JobFlag flag = JobFlag.getInstance(msg.getJobFlag());
        ServerModifyJobFlagResponse response;
        try {
            timeScheduler.modifyJobFlag(jobId, flag);
            dagScheduler.modifyJobFlag(jobId, flag);
            response = ServerModifyJobFlagResponse.newBuilder()
                    .setSuccess(true)
                    .build();
            getSender().tell(response, getSelf());
        } catch (Exception e) {
            response = ServerModifyJobFlagResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(e.getMessage())
                    .build();
            getSender().tell(response, getSelf());
            throw new IOException(e);
        }
    }

    @Transactional
    private void removeJob(RemoveJobRequest msg) throws IOException {
        long jobId = msg.getJobId();
        try {
            // remove job
            jobMapper.deleteByPrimaryKey(jobId);
            // remove job depend where preJobId=jobId
            JobDependExample jobDependExample = new JobDependExample();
            jobDependExample.createCriteria().andPreJobIdEqualTo(jobId);
            jobDependMapper.deleteByExample(jobDependExample);
            // remove crontab where jobId=jobId
            cronService.deleteByJobId(jobId);
            // scheduler remove job
            timeScheduler.removeJob(jobId);
            dagScheduler.removeJob(jobId);
            getSender().tell("remove success", getSelf());
        } catch (Exception e) {
            getSender().tell("remove failed", getSelf());
            throw new IOException(e);
        }
    }

    private void queryJobRelation(RestServerQueryJobRelationRequest msg) throws IOException {
        long jobId = msg.getJobId();
        ServerQueryJobRelationResponse.Builder builder;
        ServerQueryJobRelationResponse response;
        if (msg.getRelationType().equals(RelationType.PARENTS)) {
            try {
                builder = ServerQueryJobRelationResponse.newBuilder();
                List<Pair<Long, JobFlag>> parents = dagScheduler.getParents(jobId);
                for (Pair<Long, JobFlag> parent : parents) {
                    JobFlagEntry entry = JobFlagEntry.newBuilder()
                            .setJobId(parent.getFirst())
                            .setJobFlag(parent.getSecond().getValue())
                            .build();
                    builder.addJobFlagEntry(entry);
                }
                response = builder.setSuccess(true).build();
                getSender().tell(response, getSelf());
            } catch (Exception e) {
                response = ServerQueryJobRelationResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage(e.getMessage())
                        .build();
                getSender().tell(response, getSelf());
                throw new IOException(e);
            }
        } else if (msg.getRelationType().equals(RelationType.CHILDREN)) {
            try {
                builder = ServerQueryJobRelationResponse.newBuilder();
                List<Pair<Long, JobFlag>> children = dagScheduler.getChildren(jobId);
                for (Pair<Long, JobFlag> child : children) {
                    JobFlagEntry entry = JobFlagEntry.newBuilder()
                            .setJobId(child.getFirst())
                            .setJobFlag(child.getSecond().getValue())
                            .build();
                    builder.addJobFlagEntry(entry);
                }
                response = builder.setSuccess(true).build();
                getSender().tell(response, getSelf());
            } catch (Exception e) {
                response = ServerQueryJobRelationResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage(e.getMessage())
                        .build();
                getSender().tell(response, getSelf());
                throw new IOException(e);
            }
        }
    }

    public static Set<Class<?>> handledMessages() {
        Set<Class<?>> set = new HashSet<>();
        set.add(RestServerSubmitJobRequest.class);
        set.add(RestServerModifyJobRequest.class);
        set.add(RestServerModifyJobFlagRequest.class);
        return set;
    }
}
