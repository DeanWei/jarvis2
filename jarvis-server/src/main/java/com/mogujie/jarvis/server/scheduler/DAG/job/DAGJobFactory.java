/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午9:02:36
 */

package com.mogujie.jarvis.server.scheduler.dag.job;

import com.mogujie.jarvis.core.common.util.ReflectionUtils;
import com.mogujie.jarvis.server.scheduler.JobScheduleType;
import com.mogujie.jarvis.server.scheduler.dag.JobDependencyStrategy;
import com.mogujie.jarvis.server.scheduler.dag.status.IJobDependStatus;

/**
 * @author guangming
 *
 */
public class DAGJobFactory {
    public static DAGJob createDAGJob(JobScheduleType jobType, int jobid,
            IJobDependStatus jobstatus, JobDependencyStrategy dependStrategy) throws ClassNotFoundException {
        String className = jobType.getValue();
        DAGJob dagJob = ReflectionUtils.getClassByName(className);
        if (dagJob != null) {
            dagJob.setJobid(jobid);
            dagJob.setJobstatus(jobstatus);
            dagJob.setDependStrategy(dependStrategy);
        }

        return dagJob;
    }
}
