/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午2:42:32
 */

package com.mogujie.jarvis.server.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.Configuration;

import com.google.common.collect.Sets;
import com.mogujie.jarvis.core.JobContext;
import com.mogujie.jarvis.core.common.util.ReflectionUtils;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.protocol.SubmitJobProtos.RestServerSubmitJobRequest;
import com.mogujie.jarvis.server.scheduler.dag.JobDependencyStrategy;
import com.mogujie.jarvis.server.scheduler.dag.status.IJobDependStatus;
import com.mogujie.jarvis.server.scheduler.dag.status.MysqlCachedJobDependStatus;

/**
 * @author guangming
 *
 */
public class SchedulerUtil {
    public static String JOB_DEPEND_STATUS_KEY = "job.depend.status";
    public static String DEFAULT_JOB_DEPEND_STATUS = MysqlCachedJobDependStatus.class.getName();

    public static IJobDependStatus getJobDependStatus(Configuration conf) throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        String className = conf.getString(JOB_DEPEND_STATUS_KEY, DEFAULT_JOB_DEPEND_STATUS);
        return ReflectionUtils.getClassByName(className);
    }

    public static JobContext getJobContext(long jobid) {
        // TODO
        return null;
    }

    public static JobDescriptor convert2JobDesc(RestServerSubmitJobRequest msg) {
        Job job = new Job();
        // TODO
        Set<Long> needDependencies = Sets.newHashSet();
        needDependencies.addAll(msg.getDependencyJobidsList());
        // TODO
        JobScheduleType type = JobScheduleType.CRONTAB;
        if (job.getContent() != null) {
            if (needDependencies.isEmpty()) {
                type = JobScheduleType.CRONTAB;
            } else {
                type = JobScheduleType.CRON_DEPEND;
            }
        } else {
            if (!needDependencies.isEmpty()) {
                type = JobScheduleType.DEPENDENCY;
            } else {
                type = JobScheduleType.OTHER;
            }
        }
        JobDependencyStrategy strategy = JobDependencyStrategy.ALL;

        return new JobDescriptor(job, needDependencies, type, strategy);
    }

    public static List<Long> getDependencyIds(long jobid) {
        List<Long> dependencies = new ArrayList<Long>();
        // TODO
        return dependencies;
    }

}
