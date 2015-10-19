/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月15日 下午2:35:11
 */
package com.mogujie.jarvis.server.service;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.dao.JobMapper;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.dto.JobExample;

/**
 * @author wuya
 *
 */
@Service
public class JobService {

    @Autowired
    private JobMapper jobMapper;

    public List<Job> getNotDeletedJobs() {
        JobExample example = new JobExample();
        example.createCriteria().andJobFlagNotEqualTo(JobFlag.DELETED.getValue());
        return jobMapper.selectByExample(example);
    }

    public boolean hasFixedDelay(long jobId) {
        Job job = jobMapper.selectByPrimaryKey(jobId);
        Preconditions.checkNotNull(job, "Job " + jobId +" not found!");
        if (job.getFixedDelay() != null && job.getFixedDelay() > 0) {
            return true;
        }
        return false;
    }

    public List<Job> getActiveExpiredJobs() {
        JobExample example = new JobExample();
        DateTime dt = DateTime.now();
        List<Integer> activeJobFlags = Lists.newArrayList(JobFlag.ENABLE.getValue(),
                JobFlag.DISABLE.getValue());
        example.createCriteria().andActiveEndDateLessThan(dt.toDate()).andJobFlagIn(activeJobFlags);
        return jobMapper.selectByExample(example);
    }

    public void updateJobFlag(Job record, String user, int newFlag) {
        record.setJobFlag(newFlag);
        record.setUpdateUser(user);
        DateTime dt = DateTime.now();
        Date currentTime = dt.toDate();
        record.setUpdateTime(currentTime);
        jobMapper.updateByPrimaryKey(record);
    }
}