/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2016年1月13日 下午2:45:37
 */

package com.mogujie.jarvis.worker;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;
import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.protocol.ReportTaskStatusProtos.WorkerReportTaskStatusRequest;
import com.mogujie.jarvis.worker.domain.TaskEntry;
import com.mogujie.jarvis.worker.status.TaskStateStore;
import com.mogujie.jarvis.worker.status.TaskStateStoreFactory;
import com.mogujie.jarvis.worker.status.TaskStatusLookup;
import com.mogujie.jarvis.worker.util.TaskConfigUtils;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;

public class TaskStateRestore extends Thread {

    private ActorSystem system;
    private Map<String, TaskEntry> taskEntryMap = TaskConfigUtils.getRegisteredTasks();
    private TaskStateStore taskStateStore = TaskStateStoreFactory.getInstance();
    private Map<TaskDetail, Integer> taskDetailMap = taskStateStore.restore();
    private Map<String, TaskStatusLookup> taskStatusLookupMap = Maps.newHashMap();
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String SERVER_AKKA_PATH = ConfigUtils.getWorkerConfig().getString(WorkerConfigKeys.SERVER_AKKA_PATH)
            + JarvisConstants.SERVER_AKKA_USER_PATH;

    public TaskStateRestore(ActorSystem system) {
        this.system = system;
    }

    @Override
    public void run() {
        if (taskDetailMap.size() == 0) {
            LOGGER.info("Don't need to restore task status");
            return;
        }

        LOGGER.info("Restoring task status: {} task(s)", taskDetailMap.size());
        Configuration config = ConfigUtils.getServerConfig();
        ActorSelection serverActor = system.actorSelection(SERVER_AKKA_PATH);
        while (taskDetailMap.size() > 0) {
            Iterator<Entry<TaskDetail, Integer>> it = taskDetailMap.entrySet().iterator();
            while (it.hasNext()) {
                Entry<TaskDetail, Integer> entry = it.next();
                TaskDetail taskDetail = entry.getKey();
                int taskStatus = entry.getValue();
                String fullId = taskDetail.getFullId();
                String taskType = taskDetail.getTaskType();

                TaskEntry taskEntry = taskEntryMap.get(taskType);
                if (taskEntry != null && taskEntry.getTaskStatusLookupClass() != null) {
                    TaskStatusLookup taskStatusLookup = taskStatusLookupMap.get(taskType);
                    if (taskStatusLookup == null) {
                        try {
                            taskStatusLookup = (TaskStatusLookup) Class.forName(taskEntry.getTaskStatusLookupClass()).newInstance();
                            taskStatusLookup.init(config);
                            taskStatusLookupMap.put(taskType, taskStatusLookup);
                        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                            LOGGER.error("", e);
                        }
                    }

                    int lookupStatus = taskStatusLookup.lookup(taskDetail);
                    if (lookupStatus == TaskStatus.SUCCESS.getValue() || lookupStatus == TaskStatus.FAILED.getValue()
                            || lookupStatus == TaskStatus.KILLED.getValue()) {
                        it.remove();
                        taskStateStore.delete(fullId);
                    }

                    WorkerReportTaskStatusRequest request = WorkerReportTaskStatusRequest.newBuilder().setFullId(fullId).setStatus(taskStatus)
                            .build();
                    serverActor.tell(request, ActorRef.noSender());
                } else {
                    WorkerReportTaskStatusRequest request = WorkerReportTaskStatusRequest.newBuilder().setFullId(fullId).setStatus(taskStatus)
                            .build();
                    serverActor.tell(request, ActorRef.noSender());
                    it.remove();
                    taskStateStore.delete(fullId);
                }
            }
        }

        for (Entry<String, TaskStatusLookup> entry : taskStatusLookupMap.entrySet()) {
            entry.getValue().close();
        }

        LOGGER.info("Finished restoring task status");
    }
}
