/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月20日 上午11:02:58
 */

package com.mogujie.jarvis.server;

import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.lmax.disruptor.WorkHandler;
import com.mogujie.jarvis.core.Task;
import com.mogujie.jarvis.core.domain.WorkerInfo;
import com.mogujie.jarvis.protocol.MapEntryProtos.MapEntry;
import com.mogujie.jarvis.protocol.SubmitJobProtos.ServerSubmitTaskRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.WorkerSubmitTaskResponse;
import com.mogujie.jarvis.server.domain.TaskEvent;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

/**
 * 
 *
 */
@Repository
public class TaskEventHandler implements WorkHandler<TaskEvent> {

    @Autowired
    @Qualifier("roundRobinWorkerSelector")
    private WorkerSelector workerSelector;

    private ActorSystem system = JarvisServerActorSystem.getInstance();

    private static final Timeout TIMEOUT = new Timeout(Duration.create(30, TimeUnit.SECONDS));
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onEvent(TaskEvent event) throws Exception {
        Task task = event.getTask();
        ServerSubmitTaskRequest.Builder builder = ServerSubmitTaskRequest.newBuilder();
        builder = builder.setFullId(task.getFullId());
        builder = builder.setTaskName(task.getTaskName());
        builder = builder.setAppName(task.getAppName());
        builder = builder.setUser(task.getUser());
        builder = builder.setTaskType(task.getTaskType());
        builder = builder.setCommand(task.getCommand());
        builder = builder.setPriority(task.getPriority());

        int i = 0;
        for (Entry<String, Object> entry : task.getParameters().entrySet()) {
            MapEntry mapEntry = MapEntry.newBuilder().setKey(entry.getKey()).setValue(entry.getValue().toString()).build();
            builder.setParameters(i++, mapEntry);
        }
        ServerSubmitTaskRequest request = builder.build();

        WorkerInfo workerInfo = workerSelector.select(task.getGroupId());
        if (workerInfo != null) {
            ActorSelection actorSelection = system.actorSelection(workerInfo.getAkkaPath());
            Future<Object> future = Patterns.ask(actorSelection, request, TIMEOUT);
            try {
                WorkerSubmitTaskResponse response = (WorkerSubmitTaskResponse) Await.result(future, TIMEOUT.duration());
                if (response.getSuccess()) {
                    if (response.getAccept()) {
                        LOGGER.debug("Task[{}] was accepted by worker[{}:{}]", task.getFullId(), workerInfo.getIp(), workerInfo.getPort());
                    } else {
                        LOGGER.warn("Task[{}] was rejected by worker[{}:{}]", task.getFullId(), workerInfo.getIp(), workerInfo.getPort());
                    }
                } else {
                    LOGGER.error("Send ServerSubmitTaskRequest error: " + response.getMessage());
                }
            } catch (Exception e) {
                LOGGER.error("Send ServerSubmitTaskRequest error", e);
            }
        } else {
            LOGGER.warn("Can not select workerinfo for task: " + task.getFullId());
        }
    }

}
