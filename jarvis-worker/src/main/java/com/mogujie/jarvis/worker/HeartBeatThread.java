/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月1日 下午3:01:04
 */

package com.mogujie.jarvis.worker;

import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.protocol.HeartBeatProtos.HeartBeatRequest;
import com.mogujie.jarvis.protocol.HeartBeatProtos.HeartBeatResponse;
import com.mogujie.jarvis.protocol.WorkerProtos.ServerRegistryResponse;
import com.mogujie.jarvis.protocol.WorkerProtos.WorkerRegistryRequest;
import com.mogujie.jarvis.worker.util.FutureUtils;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;

/**
 * @author wuya
 *
 */
public class HeartBeatThread extends Thread {

    private ActorSelection heartBeatActor;
    private ActorRef sender;
    private TaskPool taskPool = TaskPool.INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();

    public HeartBeatThread(ActorSelection heartBeatActor, ActorRef sender) {
        this.heartBeatActor = heartBeatActor;
        this.sender = sender;
    }

    private void registerWorker() {
        Configuration workerConfig = ConfigUtils.getWorkerConfig();
        int workerGroupId = workerConfig.getInt(WorkerConfigKeys.WORKER_GROUP_ID, 0);
        String workerKey = workerConfig.getString(WorkerConfigKeys.WORKER_KEY);
        WorkerRegistryRequest request = WorkerRegistryRequest.newBuilder().setKey(workerKey).build();

        try {
            ServerRegistryResponse response = (ServerRegistryResponse) FutureUtils.awaitResult(heartBeatActor, request, 30);
            if (!response.getSuccess()) {
                LOGGER.error("Worker register failed with group.id={}, worker.key={}", workerGroupId, workerKey);
                return;
            } else {
                LOGGER.info("Worker register successful");
            }
        } catch (Exception e) {
            LOGGER.error("Worker register failed", e);
            return;
        }
    }

    @Override
    public void run() {
        int jobNum = taskPool.size();
        HeartBeatRequest request = HeartBeatRequest.newBuilder().setJobNum(jobNum).build();
        heartBeatActor.tell(request, sender);
        try {
            HeartBeatResponse response = (HeartBeatResponse) FutureUtils.awaitResult(heartBeatActor, request, 60);
            if (!response.getSuccess()) {
                registerWorker();
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }
}
