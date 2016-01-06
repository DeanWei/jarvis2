/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年11月5日 下午3:47:31
 */

package com.mogujie.jarvis.server.scheduler.time;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import org.joda.time.DateTime;

import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.core.domain.TaskType;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.service.TaskService;
import com.mogujie.jarvis.server.util.PlanUtil;

public enum TimePlan {
    INSTANCE;

    private Comparator<TimePlanEntry> comparator = new Comparator<TimePlanEntry>() {

        @Override
        public int compare(TimePlanEntry o1, TimePlanEntry o2) {
            return o1.getDateTime().compareTo(o2.getDateTime());
        }
    };

    private Queue<TimePlanEntry> plan = new PriorityQueue<>(comparator);

    public synchronized boolean addPlan(long jobId, DateTime dateTime) {
        return plan.add(new TimePlanEntry(jobId, dateTime));
    }

    public synchronized boolean addPlan(TimePlanEntry entry) {
        if (!plan.contains(entry)) {
            return plan.add(entry);
        } else {
            return false;
        }
    }

    public synchronized boolean removePlan(TimePlanEntry planEntry) {
        return plan.remove(planEntry);
    }

    public synchronized void addJob(long jobId) {
        DateTime nextTime = PlanUtil.getScheduleTimeAfter(jobId, DateTime.now());
        addPlan(jobId, nextTime);
    }

    public synchronized void recoverJob(long jobId) {
        long scheduleTime = DateTime.now().getMillis();
        TaskService taskService = Injectors.getInjector().getInstance(TaskService.class);
        Task lastone = taskService.getLastTask(jobId, scheduleTime, TaskType.SCHEDULE);
        if (lastone != null) {
            scheduleTime = lastone.getScheduleTime().getTime();
        }
        DateTime nextTime = PlanUtil.getScheduleTimeAfter(jobId, new DateTime(scheduleTime));
        addPlan(jobId, nextTime);
    }

    public synchronized void removeJob(long jobId) {
        Iterator<TimePlanEntry> it = plan.iterator();
        while (it.hasNext()) {
            TimePlanEntry entry = it.next();
            if (entry.getJobId() == jobId) {
                it.remove();
            }
        }
    }

    public synchronized void modifyJobFlag(long jobId, JobStatus oldStatus, JobStatus newStatus) {
        if (newStatus.equals(JobStatus.DISABLE) || newStatus.equals(JobStatus.DELETED)) {
            removeJob(jobId);
        } else if (newStatus.equals(JobStatus.ENABLE)) {
            if (oldStatus.equals(JobStatus.PAUSE)) {
                //如果从暂停状态恢复过来，要把之前的没跑过的都恢复回来
                recoverJob(jobId);
            } else if (oldStatus.equals(JobStatus.DISABLE) || oldStatus.equals(JobStatus.DELETED)) {
                //如果是从禁用或废弃状态恢复回来，不需要恢复历史任务，从当前时间计算下一次
                addJob(jobId);
            } else if (oldStatus.equals(JobStatus.ENABLE)) {
                // nothing to do
            }
        }
    }

    public synchronized Queue<TimePlanEntry> getPlan() {
        return plan;
    }

    public synchronized void clear() {
        plan.clear();
    }

}
