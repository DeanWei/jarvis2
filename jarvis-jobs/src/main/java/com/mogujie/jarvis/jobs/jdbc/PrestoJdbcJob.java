/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午10:06:30
 */

package com.mogujie.jarvis.jobs.jdbc;

import org.apache.commons.configuration.Configuration;

import com.mogujie.jarvis.core.JobContext;

/**
 * @author guangming
 *
 */
public class PrestoJdbcJob extends JdbcJob {

    /**
     * @param jobContext
     */
    public PrestoJdbcJob(JobContext jobContext) {
        super(jobContext);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected String getJobType() {
        return "presto";
    }

    @Override
    protected String getDriverName() {
        return "com.facebook.presto.jdbc.PrestoDriver";
    }

    @Override
    protected String getJdbcUrl(Configuration conf) {
        return conf.getString("presto.jdbc.url");
    }

    @Override
    protected int getMaxQueryRows(Configuration conf) {
        return conf.getInt("presto.max.query.rows", DEFAULT_MAX_QUERY_ROWS);
    }
}
