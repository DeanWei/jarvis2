/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午2:13:48
 */

package com.mogujie.jarvis.server.scheduler.dag.status;

/**
 * @author guangming
 *
 */
public class MysqlCachedDependStatus extends CachedDependStatus {

    @Override
    public void setDependStatus(long jobId, long taskId) {
        super.setDependStatus(jobId, taskId);
        flush2DB();
    }

    @Override
    public void resetDependStatus(long jobId, long taskId) {
        super.resetDependStatus(jobId, taskId);
        flush2DB();
    }

    @Override
    public void removeDependency(long jobId) {
        super.removeDependency(jobId);
        flush2DB();
    }

    @Override
    public void reset() {
        super.reset();
        flush2DB();
    }

    public void flush2DB() {
        //TODO flush to mysql
    }
}
