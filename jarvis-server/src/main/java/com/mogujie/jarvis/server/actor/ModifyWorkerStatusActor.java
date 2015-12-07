/*
 * 蘑菇街 Inc. Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya Create Date: 2015年9月22日 下午3:25:50
 */

package com.mogujie.jarvis.server.actor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.mogujie.jarvis.core.domain.ActorEntry;
import com.mogujie.jarvis.core.domain.MessageType;
import com.mogujie.jarvis.dao.generate.WorkerMapper;
import com.mogujie.jarvis.dto.generate.Worker;
import com.mogujie.jarvis.dto.generate.WorkerExample;
import com.mogujie.jarvis.protocol.ModifyWorkerStatusProtos.RestServerModifyWorkerStatusRequest;
import com.mogujie.jarvis.protocol.ModifyWorkerStatusProtos.ServerModifyWorkerStatusResponse;

import akka.actor.UntypedActor;

@Named("modifyWorkerStatusActor")
@Scope("prototype")
public class ModifyWorkerStatusActor extends UntypedActor {

    @Autowired
    private WorkerMapper workerMapper;

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof RestServerModifyWorkerStatusRequest) {
            RestServerModifyWorkerStatusRequest request = (RestServerModifyWorkerStatusRequest) obj;
            String ip = request.getIp();
            int port = request.getPort();
            int status = request.getStatus();

            ServerModifyWorkerStatusResponse response = ServerModifyWorkerStatusResponse.newBuilder().setSuccess(true).setMessage("").build();
            getSender().tell(response, getSelf());

            Worker worker = new Worker();
            worker.setStatus(status);

            WorkerExample example = new WorkerExample();
            example.createCriteria().andIpEqualTo(ip).andPortEqualTo(port);
            workerMapper.updateByExample(worker, example);
        } else {
            unhandled(obj);
        }
    }

    public static List<ActorEntry> handledMessages() {
        List<ActorEntry> list = new ArrayList<>();
        list.add(new ActorEntry(RestServerModifyWorkerStatusRequest.class, ServerModifyWorkerStatusResponse.class, MessageType.SYSTEM));
        return list;
    }

}
