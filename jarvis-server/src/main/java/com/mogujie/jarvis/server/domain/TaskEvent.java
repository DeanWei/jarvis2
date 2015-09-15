/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月15日 下午5:15:39
 */

package com.mogujie.jarvis.server.domain;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.mogujie.jarvis.core.Task;

/**
 * 
 *
 */
public class TaskEvent {

    private Task task;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public static final EventTranslatorOneArg<TaskEvent, Task> TRANSLATOR = new EventTranslatorOneArg<TaskEvent, Task>() {

        public void translateTo(TaskEvent event, long sequence, Task task) {
            event.setTask(task);
        }
    };

    public static final EventFactory<TaskEvent> EVENT_FACTORY = new EventFactory<TaskEvent>() {

        public TaskEvent newInstance() {
            return new TaskEvent();
        }
    };
}
