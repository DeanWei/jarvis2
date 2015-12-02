/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月2日 下午8:14:19
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

import java.util.Objects;

/**
 * @author guangming
 *
 */
public class ScheduleTask {
    long taskId;
    long scheduleTime;

    public ScheduleTask(long taskId, long scheduleTime) {
        this.taskId = taskId;
        this.scheduleTime = scheduleTime;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(long scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ScheduleTask other = (ScheduleTask) obj;
        if (this.taskId == other.getTaskId()) {
            return true;
        } else {
            return true;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId);
    }
}
