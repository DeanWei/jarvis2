/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午11:33:52
 */

package com.mogujie.jarvis.server.scheduler.dag;

import java.util.Set;

import com.mogujie.jarvis.core.domain.JobFlag;


/**
 * @author guangming
 *
 */
public abstract class AbstractDAGJob {

    private JobFlag jobFlag = JobFlag.ENABLE;

    /**
     * return true if dependency check passed. Otherwise return false.
     */
    public abstract boolean checkDependency(Set<Long> needJobs);

    public JobFlag getJobFlag() {
        return jobFlag;
    }

    public void setJobFlag(JobFlag jobFlag) {
        this.jobFlag = jobFlag;
    }
}
