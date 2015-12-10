/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年12月3日 下午1:49:42
 */

package com.mogujie.jarvis.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.core.util.IdUtils;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.protocol.AppAuthProtos;
import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.protocol.QueryTaskRelationProtos.RestServerQueryTaskRelationRequest;
import com.mogujie.jarvis.protocol.QueryTaskRelationProtos.ServerQueryTaskRelationResponse;
import com.mogujie.jarvis.protocol.QueryTaskRelationProtos.TaskMapEntry;
import com.mogujie.jarvis.protocol.KillTaskProtos.RestServerKillTaskRequest;
import com.mogujie.jarvis.protocol.KillTaskProtos.ServerKillTaskResponse;
import com.mogujie.jarvis.protocol.ManualRerunTaskProtos.RestServerManualRerunTaskRequest;
import com.mogujie.jarvis.protocol.ManualRerunTaskProtos.ServerManualRerunTaskResponse;
import com.mogujie.jarvis.protocol.ModifyTaskStatusProtos.RestServerModifyTaskStatusRequest;
import com.mogujie.jarvis.protocol.ModifyTaskStatusProtos.ServerModifyTaskStatusResponse;
import com.mogujie.jarvis.protocol.RetryTaskProtos.RestServerRetryTaskRequest;
import com.mogujie.jarvis.protocol.RetryTaskProtos.ServerRetryTaskResponse;
import com.mogujie.jarvis.protocol.SubmitTaskProtos.RestServerSubmitTaskRequest;
import com.mogujie.jarvis.protocol.SubmitTaskProtos.ServerSubmitTaskResponse;
import com.mogujie.jarvis.rest.RestResult;
import com.mogujie.jarvis.rest.utils.JsonParameters;
import com.mogujie.jarvis.rest.vo.RerunTaskVo;
import com.mogujie.jarvis.rest.vo.TaskEntryVo;
import com.mogujie.jarvis.rest.vo.TaskRelationsVo;
import com.mogujie.jarvis.rest.vo.TaskVo;
import com.mogujie.jarvis.core.domain.JobRelationType;

/**
 * @author guangming
 *
 */
@Path("api/task")
public class TaskController extends AbstractController {

    /**
     * 根据fullId kill task
     *
     * @throws Exception
     */
    @POST
    @Path("kill")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult kill(@FormParam("user") String user, @FormParam("appToken") String appToken, @FormParam("appName") String appName,
            @FormParam("parameters") String parameters) {
        try {
            AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JsonParameters para = new JsonParameters(parameters);
            long jobId = para.getLong("jobId");
            long taskId = para.getLong("taskId");
            int attemptId = para.getInteger("attemptId");
            String fullId = IdUtils.getFullId(jobId, taskId, attemptId);

            RestServerKillTaskRequest request = RestServerKillTaskRequest.newBuilder().setAppAuth(appAuth).setFullId(fullId).build();

            ServerKillTaskResponse response = (ServerKillTaskResponse) callActor(AkkaType.SERVER, request);
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

    /**
     * 根据taskId原地重试task，按照历史依赖关系
     *
     * @throws Exception
     */
    @POST
    @Path("retry")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult retry(@FormParam("user") String user, @FormParam("appToken") String appToken, @FormParam("appName") String appName,
            @FormParam("parameters") String parameters) {
        try {
            AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JsonParameters para = new JsonParameters(parameters);
            long taskId = para.getLong("taskId");

            RestServerRetryTaskRequest request = RestServerRetryTaskRequest.newBuilder().setAppAuth(appAuth).setTaskId(taskId).build();

            ServerRetryTaskResponse response = (ServerRetryTaskResponse) callActor(AkkaType.SERVER, request);
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

    /**
     * 给定jobId和一段时间，手动重跑任务，按照新的依赖关系，支持是否重跑后续任务
     *
     * @throws Exception
     */
    @POST
    @Path("rerun")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult rerun(@FormParam("user") String user, @FormParam("appToken") String appToken, @FormParam("appName") String appName,
            @FormParam("parameters") String parameters) {
        try {
            AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            RerunTaskVo rerunVo = JsonHelper.fromJson(parameters, RerunTaskVo.class);
            List<Long> jobIdList = rerunVo.getJobIdList();
            long startDate = rerunVo.getStartDate();
            long endDate = rerunVo.getEndDate();
            boolean runChild = rerunVo.isRunChild();

            RestServerManualRerunTaskRequest.Builder builder = RestServerManualRerunTaskRequest.newBuilder();
            for (long jobId : jobIdList) {
                builder.addJobId(jobId);
            }
            RestServerManualRerunTaskRequest request = builder.setAppAuth(appAuth).setStartTime(startDate).setEndTime(endDate).setRunChild(runChild)
                    .build();

            ServerManualRerunTaskResponse response = (ServerManualRerunTaskResponse) callActor(AkkaType.SERVER, request);
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

    /**
     * 提交一次性任务
     *
     * @throws Exception
     */
    @POST
    @Path("submit")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult submit(@FormParam("user") String user, @FormParam("appToken") String appToken, @FormParam("appName") String appName,
            @FormParam("parameters") String parameters) {
        try {
            AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            TaskEntryVo taskVo = JsonHelper.fromJson(parameters, TaskEntryVo.class);
            String jobParameters = "";
            if (taskVo.getParams() != null) {
                jobParameters = JsonHelper.toJson(taskVo.getParams(), Map.class);
            }
            RestServerSubmitTaskRequest request = RestServerSubmitTaskRequest.newBuilder().setAppAuth(appAuth).setTaskName(taskVo.getTaskName())
                    .setContent(taskVo.getContent()).setTaskType(taskVo.getTaskType()).setUser(taskVo.getUser()).setGroupId(taskVo.getGroupId())
                    .setPriority(taskVo.getPriority(0)).setRejectRetries(taskVo.getRejectRetries(0)).setRejectInterval(taskVo.getRejectInterval(3))
                    .setFailedRetries(taskVo.getFailedRetries(0)).setFailedInterval(taskVo.getFailedInterval(3)).setParameters(jobParameters).build();

            ServerSubmitTaskResponse response = (ServerSubmitTaskResponse) callActor(AkkaType.SERVER, request);
            if (response.getSuccess()) {
                TaskVo vo = new TaskVo();
                vo.setTaskId(response.getTaskId());
                return successResult(vo);
            } else {
                return errorResult(response.getMessage());
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return errorResult(e.getMessage());
        }
    }

    /**
     * 强制修改task的状态（慎用！！仅限管理员使用！！）
     *
     * @throws Exception
     */
    @POST
    @Path("modify/status")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult modifyStatus(@FormParam("user") String user, @FormParam("appToken") String appToken, @FormParam("appName") String appName,
            @FormParam("parameters") String parameters) {
        try {
            AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JsonParameters para = new JsonParameters(parameters);
            long taskId = para.getLong("taskId");
            int status = para.getInteger("status");

            RestServerModifyTaskStatusRequest request = RestServerModifyTaskStatusRequest.newBuilder().setAppAuth(appAuth).setTaskId(taskId)
                    .setStatus(status).build();

            ServerModifyTaskStatusResponse response = (ServerModifyTaskStatusResponse) callActor(AkkaType.SERVER, request);
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

    /**
     * 查找Job关系
     */
    @POST
    @Path("queryRelation")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult queryRelation(@FormParam("user") String user,
                                       @FormParam("appToken") String appToken,
                                       @FormParam("appName") String appName,
                                       @FormParam("parameters") String parameters) {
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JsonParameters paras = new JsonParameters(parameters);
            Long jobId = paras.getLongNotNull("taskId");
            Integer relationType = paras.getIntegerNotNull("relationType");
            if(!JobRelationType.isValid(relationType)){
                throw new IllegalArgumentException("参数不对。key='relationType',value=" + relationType.toString());
            }

            RestServerQueryTaskRelationRequest request =RestServerQueryTaskRelationRequest.newBuilder()
                    .setAppAuth(appAuth)
                    .setTaskId(jobId)
                    .setRelationType(relationType)
                    .build();

            ServerQueryTaskRelationResponse response = (ServerQueryTaskRelationResponse) callActor(AkkaType.SERVER, request);
            if (response.getSuccess()) {
                TaskRelationsVo vo = new TaskRelationsVo();
                if(response.getTaskRelationMapList() != null){
                    List<TaskRelationsVo.RelationEntry> list = new ArrayList<>();
                    for(TaskMapEntry entry : response.getTaskRelationMapList()){
                        list.add(new TaskRelationsVo.RelationEntry().setJobId(entry.getJobId()).setTaskIds(entry.getTaskIdList()));
                    }
                    vo.setList(list);
                }
                return successResult(vo);
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return errorResult(e.getMessage());
        }
    }


}
