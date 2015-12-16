/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月10日 上午11:21:23
 */

package com.mogujie.jarvis.server.actor;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mogujie.jarvis.core.domain.MessageType;
import com.mogujie.jarvis.core.domain.WorkerInfo;
import com.mogujie.jarvis.protocol.HeartBeatProtos.HeartBeatRequest;
import com.mogujie.jarvis.protocol.HeartBeatProtos.HeartBeatResponse;
import com.mogujie.jarvis.server.WorkerRegistry;
import com.mogujie.jarvis.server.domain.ActorEntry;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.service.HeartBeatService;

import akka.actor.Address;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class HeartBeatActor extends UntypedActor {

    private HeartBeatService heartBeatService = Injectors.getInjector().getInstance(HeartBeatService.class);

    private static final Logger LOGGER = LogManager.getLogger("heartbeat");

    public static Props props() {
        return Props.create(HeartBeatActor.class);
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof HeartBeatRequest) {
            HeartBeatRequest request = (HeartBeatRequest) obj;
            Address address = getSender().path().address();
            String ip = address.host().get();
            int port = Integer.parseInt(address.port().get().toString());
            int jobNum = request.getJobNum();
            WorkerInfo workerInfo = new WorkerInfo(ip, port);
            int groupId = WorkerRegistry.getInstance().getWorkerGroupId(workerInfo);
            HeartBeatResponse response = null;
            if (groupId < 0) {
                LOGGER.warn("groupId is not valid: heartbeat[ip={}, port={}, groupId={}, jobNum={}]", ip, port, groupId, jobNum);
                //todo debug 测试临时屏蔽worker注册
                heartBeatService.put(groupId, workerInfo, jobNum);
                //todo end.......
                response = HeartBeatResponse.newBuilder().setSuccess(false).setMessage("groupId is not valid: " + groupId).build();
            } else {
                LOGGER.debug("heartbeat[ip={}, port={}, groupId={}, jobNum={}]", ip, port, groupId, jobNum);
                heartBeatService.put(groupId, workerInfo, jobNum);
                response = HeartBeatResponse.newBuilder().setSuccess(true).build();
            }
            getSender().tell(response, getSelf());
        } else {
            unhandled(obj);
        }
    }

    public static List<ActorEntry> handledMessages() {
        List<ActorEntry> list = new ArrayList<>();
        list.add(new ActorEntry(HeartBeatRequest.class, HeartBeatResponse.class, MessageType.SYSTEM));
        return list;
    }

}
