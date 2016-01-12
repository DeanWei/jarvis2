/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2016年1月11日 下午2:45:06
 */

package com.mogujie.jarvis.server.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mogujie.jarvis.dao.generate.TaskHistoryMapper;
import com.mogujie.jarvis.dto.generate.TaskHistory;
import com.mogujie.jarvis.dto.generate.TaskHistoryExample;
import com.mogujie.jarvis.dto.generate.TaskHistoryKey;

/**
 * @author guangming
 *
 */
@Singleton
public class TaskHistoryService {

    @Inject
    private TaskHistoryMapper taskHistoryMapper;

    public void insertOrUpdate(TaskHistory record) {
        TaskHistoryKey key = new TaskHistoryKey();
        key.setTaskId(record.getTaskId());
        key.setAttemptId(record.getAttemptId());
        TaskHistory history = taskHistoryMapper.selectByPrimaryKey(key);
        if (history == null) {
            taskHistoryMapper.insert(record);
        } else {
            taskHistoryMapper.updateByPrimaryKeySelective(record);
        }
    }

    public void insertSelective(TaskHistory record) {
        taskHistoryMapper.insertSelective(record);
    }

    public void deleteByTaskId(long taskId) {
        TaskHistoryExample example = new TaskHistoryExample();
        example.createCriteria().andTaskIdEqualTo(taskId);
        taskHistoryMapper.deleteByExample(example);
    }
}
