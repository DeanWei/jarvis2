/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年12月3日 下午2:00:49
 */

package com.mogujie.jarvis.rest.controller;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.protocol.AppAuthProtos;
import com.mogujie.jarvis.protocol.RemovePlanProtos.RestServerRemovePlanRequest;
import com.mogujie.jarvis.protocol.RemovePlanProtos.ServerRemovePlanResponse;
import com.mogujie.jarvis.rest.RestResult;

/**
 * @author guangming
 *
 */
@Path("api/plan")
public class PlanController extends AbstractController {

    /**
     * 删除某条已经存在的执行计划
     *
     * @throws Exception
     */
    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult<?> delete(@FormParam("user") String user, @FormParam("appToken") String appToken, @FormParam("appName") String appName,
            @FormParam("parameters") String parameters) {
        try {
            AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JSONObject para = new JSONObject(parameters);
            long jobId = para.getLong("jobId");
            long taskId = para.getLong("taskId");
            long scheduleTime = para.getInt("time");
            boolean ask = para.getBoolean("ask");

            RestServerRemovePlanRequest request = RestServerRemovePlanRequest.newBuilder().setAppAuth(appAuth).setJobId(jobId).setTaskId(taskId)
                    .setScheduleTime(scheduleTime).setAsk(ask).build();

            ServerRemovePlanResponse response = (ServerRemovePlanResponse) callActor(AkkaType.SERVER, request);
            if (response.getSuccess()) {
                return successResult();
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return errorResult(e.getMessage());
        }
    }
}
