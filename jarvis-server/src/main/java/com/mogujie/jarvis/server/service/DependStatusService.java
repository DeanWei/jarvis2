/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月10日 上午10:13:36
 */

package com.mogujie.jarvis.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mogujie.jarvis.dao.JobDependStatusMapper;
import com.mogujie.jarvis.dto.JobDependStatus;
import com.mogujie.jarvis.dto.JobDependStatusExample;
import com.mogujie.jarvis.dto.JobDependStatusKey;
import com.mogujie.jarvis.server.domain.JobKey;

/**
 * @author guangming
 *
 */
@Service
public class DependStatusService {
    @Autowired
    private JobDependStatusMapper dependStatusMapper;

    public JobDependStatus getByKey(JobDependStatusKey key) {
        return dependStatusMapper.selectByPrimaryKey(key);
    }

    public void insert(JobDependStatus record) {
        dependStatusMapper.insert(record);
    }

    public void update(JobDependStatus record) {
        dependStatusMapper.updateByPrimaryKey(record);
    }

    public void clearMyStatusByJobId(long myJobId) {
        JobDependStatusExample example = new JobDependStatusExample();
        example.createCriteria().andJobIdEqualTo(myJobId);
        dependStatusMapper.deleteByExample(example);
    }

    public void clearMyStatusByJobKey(JobKey myJobKey) {
        JobDependStatusExample example = new JobDependStatusExample();
        example.createCriteria().andJobIdEqualTo(myJobKey.getJobId()).andJobVersionEqualTo(myJobKey.getVersion());
        dependStatusMapper.deleteByExample(example);
    }

    public void clearPreStatus(JobKey myJobKey, JobKey preJobKey) {
        JobDependStatusExample example = new JobDependStatusExample();
        example.createCriteria().andJobIdEqualTo(myJobKey.getJobId())
            .andJobVersionEqualTo(myJobKey.getVersion())
            .andPreJobIdEqualTo(preJobKey.getJobId())
            .andPreJobVersionEqualTo(preJobKey.getVersion());
        dependStatusMapper.deleteByExample(example);
    }

    public List<JobDependStatus> getRecordsByMyJobKey(JobKey myJobKey) {
        JobDependStatusExample example = new JobDependStatusExample();
        example.createCriteria().andJobIdEqualTo(myJobKey.getJobId()).andJobVersionEqualTo(myJobKey.getVersion());
        return dependStatusMapper.selectByExample(example);
    }

    public List<JobDependStatus> getRecordsByPreJobKey(JobKey myJobKey, JobKey preJobKey) {
        JobDependStatusExample example = new JobDependStatusExample();
        example.createCriteria().andJobIdEqualTo(myJobKey.getJobId())
            .andJobVersionEqualTo(myJobKey.getVersion())
            .andPreJobIdEqualTo(preJobKey.getJobId())
            .andPreJobVersionEqualTo(preJobKey.getVersion());
        return dependStatusMapper.selectByExample(example);
    }
}
