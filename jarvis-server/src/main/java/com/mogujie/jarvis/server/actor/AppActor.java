/*
 * 蘑菇街 Inc. 
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年10月9日 下午5:14:53
 */

package com.mogujie.jarvis.server.actor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mogujie.jarvis.core.domain.OperationMode;
import com.mogujie.jarvis.core.exception.NotFoundException;
import com.mogujie.jarvis.dto.generate.AppWorkerGroup;
import com.mogujie.jarvis.server.service.AppWorkerGroupService;
import com.mogujie.jarvis.server.service.ConvertValidService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import com.mogujie.jarvis.core.domain.MessageType;
import com.mogujie.jarvis.dto.generate.App;
import com.mogujie.jarvis.protocol.ApplicationProtos.RestCreateApplicationRequest;
import com.mogujie.jarvis.protocol.ApplicationProtos.ServerCreateApplicationResponse;
import com.mogujie.jarvis.protocol.ApplicationProtos.RestModifyApplicationRequest;
import com.mogujie.jarvis.protocol.ApplicationProtos.ServerModifyApplicationResponse;
import com.mogujie.jarvis.protocol.ApplicationProtos.RestSetApplicationWorkerGroupRequest;
import com.mogujie.jarvis.protocol.ApplicationProtos.ServerSetApplicationWorkerGroupResponse;
import com.mogujie.jarvis.server.dispatcher.TaskManager;
import com.mogujie.jarvis.server.domain.ActorEntry;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.service.AppService;

import akka.actor.Props;
import akka.actor.UntypedActor;
import org.mybatis.guice.transactional.Transactional;

public class AppActor extends UntypedActor {

    private static Logger logger = LogManager.getLogger();

    private TaskManager taskManager = Injectors.getInjector().getInstance(TaskManager.class);

    private AppService appService = Injectors.getInjector().getInstance(AppService.class);
    private AppWorkerGroupService appWorkerGroupService = Injectors.getInjector().getInstance(AppWorkerGroupService.class);
    private ConvertValidService convertValidService = Injectors.getInjector().getInstance(ConvertValidService.class);


    public static Props props() {
        return Props.create(AppActor.class);
    }

    public static List<ActorEntry> handledMessages() {
        List<ActorEntry> list = new ArrayList<>();
        list.add(new ActorEntry(RestCreateApplicationRequest.class, ServerCreateApplicationResponse.class, MessageType.SYSTEM));
        list.add(new ActorEntry(RestModifyApplicationRequest.class, ServerModifyApplicationResponse.class, MessageType.SYSTEM));
        list.add(new ActorEntry(RestSetApplicationWorkerGroupRequest.class, ServerSetApplicationWorkerGroupResponse.class, MessageType.SYSTEM));
        return list;
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof RestCreateApplicationRequest) {
            createApplication((RestCreateApplicationRequest) obj);
        } else if (obj instanceof RestModifyApplicationRequest) {
            modifyApplication((RestModifyApplicationRequest) obj);
        } else if (obj instanceof RestSetApplicationWorkerGroupRequest) {
            setApplicationWorkerGroup((RestSetApplicationWorkerGroupRequest) obj);
        } else {
            unhandled(obj);
        }
    }

    @Transactional
    private void createApplication(RestCreateApplicationRequest request) {
        ServerCreateApplicationResponse response = null;
        try {
            String key = UUID.randomUUID().toString().replace("-", "");
            DateTime date = DateTime.now();
            App app = new App();
            app.setAppName(request.getAppName());
            app.setAppKey(key);
            app.setOwner(request.getOwner());
            app.setStatus(request.getStatus());
            app.setMaxConcurrency(request.getMaxConcurrency());
            app.setCreateTime(date.toDate());
            app.setUpdateTime(date.toDate());
            app.setUpdateUser(request.getUser());
            appService.insert(app);
            taskManager.addApp(app.getAppId(), request.getMaxConcurrency());
            response = ServerCreateApplicationResponse.newBuilder().setSuccess(true).build();
        } catch (Exception ex) {
            response = ServerCreateApplicationResponse.newBuilder().setSuccess(false).setMessage(ex.getMessage()).build();
            logger.error("", ex);
            throw ex;
        } finally {
            getSender().tell(response, getSelf());
        }
    }

    @Transactional
    private void modifyApplication(RestModifyApplicationRequest request) {
        ServerModifyApplicationResponse response = null;

        App app = new App();
        Integer appId = request.getAppId();
        app.setAppId(appId);
        if (request.hasAppName()) {
            app.setAppName(request.getAppName());
        }
        if (request.hasOwner()) {
            app.setOwner(request.getOwner());
        }
        if (request.hasStatus()) {
            app.setStatus(request.getStatus());
        }
        if (request.hasMaxConcurrency()) {
            app.setMaxConcurrency(request.getMaxConcurrency());
        }
        app.setUpdateTime(DateTime.now().toDate());
        app.setUpdateUser(request.getUser());

        try {
            appService.update(app);
            if (request.hasMaxConcurrency()) {
                taskManager.updateAppMaxParallelism(appId, request.getMaxConcurrency());
            }
            response = ServerModifyApplicationResponse.newBuilder().setSuccess(true).build();
        } catch (Exception ex) {
            response = ServerModifyApplicationResponse.newBuilder().setSuccess(false).setMessage(ex.getMessage()).build();
            logger.error("", ex);
            throw ex;

        } finally {
            getSender().tell(response, getSelf());
        }
    }


    @Transactional
    private void setApplicationWorkerGroup(RestSetApplicationWorkerGroupRequest request) throws NotFoundException{
        ServerSetApplicationWorkerGroupResponse response = null;
        try {

            AppWorkerGroup ag = convertValidService.convert2AppWorkeGroupByCheck(request);
            OperationMode mode = OperationMode.parseValue(request.getMode());
            if (mode == OperationMode.ADD) {
                appWorkerGroupService.insert(ag);
            } else if (mode == OperationMode.DELETE) {
                appWorkerGroupService.delete(ag.getAppId(), ag.getWorkerGroupId());
            }
            response = ServerSetApplicationWorkerGroupResponse.newBuilder().setSuccess(true).build();
        } catch (Exception ex) {
            response = ServerSetApplicationWorkerGroupResponse.newBuilder().setSuccess(false).setMessage(ex.getMessage()).build();
            logger.error("", ex);
            throw ex;
        } finally {
            getSender().tell(response, getSelf());
        }
    }


}
