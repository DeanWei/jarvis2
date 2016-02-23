/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2016年2月23日 下午1:17:37
 */

package com.mogujie.jarvis.rest.controller;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.joda.time.DateTime;

import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.protocol.SearchJobProtos.RestSearchJobByScriptIdRequest;
import com.mogujie.jarvis.protocol.SearchJobProtos.ServerSearchJobByScriptIdResponse;
import com.mogujie.jarvis.rest.jarvis.Result;
import com.mogujie.jarvis.rest.jarvis.TaskInfo;
import com.mogujie.jarvis.rest.jarvis.TaskInfoResult;
import com.mogujie.jarvis.rest.jarvis.TasksResult;
import com.mogujie.jarvis.rest.jarvis.User;
import com.mogujie.jarvis.server.util.AppTokenUtils;

/**
 * @author guangming
 *
 */
@Deprecated
@Path("api")
public class JarvisController extends AbstractController {

    private static String APP_IRONMAN_NAME = "ironman";
    private static String APP_IRONMAN_KEY = "123";
    private static String APP_XMEN_NAME = "xmen";
    private static String APP_XMEN_KEY = "456";

    @GET
    @Path("taskinfo")
    @Produces(MediaType.APPLICATION_JSON)
    public TaskInfoResult getTaskInfo(@PathParam("scriptId") int scriptId) {
        LOGGER.debug("根据scriptId查询taskinfo");
        TaskInfoResult result = new TaskInfoResult();
        try {
            String appToken = AppTokenUtils.generateToken(DateTime.now().getMillis(), APP_IRONMAN_KEY);
            AppAuth appAuth = AppAuth.newBuilder().setName(APP_IRONMAN_NAME).setToken(appToken).build();

            RestSearchJobByScriptIdRequest request = RestSearchJobByScriptIdRequest.newBuilder().setAppAuth(appAuth)
                    .setUser(APP_IRONMAN_NAME).setScriptId(scriptId).build();

            ServerSearchJobByScriptIdResponse response = (ServerSearchJobByScriptIdResponse) callActor(AkkaType.SERVER, request);

            if (response.getSuccess()) {
                result.setSuccess(true);
                //TODO
            } else {
                result.setSuccess(false);
                result.setMessage(response.getMessage());
            }
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
    }

    @GET
    @Path("alltasks")
    @Produces(MediaType.APPLICATION_JSON)
    public TasksResult getAllTasks() {
        //TODO
        return null;
    }

    @GET
    @Path("getdependencybyscript.htm")
    @Produces(MediaType.APPLICATION_JSON)
    public TasksResult getDependencyByScript(@PathParam("scriptId") int scriptId) {
        //TODO
        return null;
    }

    @GET
    @Path("sdependtasks")
    @Produces(MediaType.APPLICATION_JSON)
    public TasksResult getScriptDepend(@PathParam("scriptId") int scriptId) {
        //TODO
        return null;
    }

    @GET
    @Path("searchtask")
    @Produces(MediaType.APPLICATION_JSON)
    public TasksResult searchTask(@PathParam("keyword") String title) {
        //TODO
        return null;
    }

    @POST
    @Path("submittask")
    @Produces(MediaType.APPLICATION_JSON)
    public Result submitTask(@FormParam("task") String task, @FormParam("globalUser") String globalUser,
            User user) {
        TaskInfo taskInfo = JsonHelper.fromJson(task, TaskInfo.class);
        return null;
    }
}
