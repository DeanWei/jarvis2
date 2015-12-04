/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: muming
 * Create Date: 2015年10月08日 下午3:19:28
 */
package com.mogujie.jarvis.rest.controller;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.core.domain.StreamType;
import com.mogujie.jarvis.protocol.AppAuthProtos;
import com.mogujie.jarvis.protocol.ReadLogProtos.LogServerReadLogResponse;
import com.mogujie.jarvis.protocol.ReadLogProtos.RestServerReadLogRequest;
import com.mogujie.jarvis.rest.RestResult;
import com.mogujie.jarvis.rest.utils.JsonParameters;
import com.mogujie.jarvis.rest.vo.LogVo;

/**
 * @author muming
 */
@Path("api/log")
public class LogController extends AbstractController {

    private final static int DEFAULT_LINE = 1000;

    /**
     * 获取执行日志
     *
     * @param appName    appName
     * @param appToken   taskId
     * @param user
     * @param parameters (taskId、offset：日志内容的字节偏移量、lines：日志读取的行数)
     * @return
     * @throws Exception
     */
    @POST
    @Path("readExecuteLog")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult<?> readExecuteLog(@FormParam("appToken") String appToken, @FormParam("appName") String appName, @FormParam("user") String user,
                                    @FormParam("parameters") String parameters) {

        return _getData(StreamType.STD_ERR, appToken, appName, user, parameters);
    }

    /**
     * 获取结果数据
     *
     * @param appName    appName
     * @param appToken
     * @param user
     * @param parameters (taskId、offset：日志内容的字节偏移量、lines：日志读取的行数)
     * @return
     * @throws Exception
     */
    @POST
    @Path("readResult")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult<?> readResult(@FormParam("appToken") String appToken, @FormParam("appName") String appName, @FormParam("user") String user,
                                @FormParam("parameters") String parameters) throws Exception {
        return _getData(StreamType.STD_OUT, appToken, appName, user, parameters);
    }

    /**
     * 获取数据_内部函数
     */
    private RestResult<?> _getData(StreamType type, String appToken, String appName, String user, String parameters) {

        try {
            AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JsonParameters para = new JsonParameters(parameters);
            Long taskId = para.getLongNotNull("taskId");
            Long offset = para.getLong("offset", 0L);
            Integer lines = para.getInteger("lines", DEFAULT_LINE);

            RestServerReadLogRequest request = RestServerReadLogRequest.newBuilder().setTaskId(taskId)
                    .setOffset(offset).setType(type.getValue()).setAppAuth(appAuth).setLines(lines).build();

            LogServerReadLogResponse response = (LogServerReadLogResponse) callActor(AkkaType.LOGSTORAGE, request);

            if (response.getSuccess()) {
                LogVo logVo = new LogVo();
                logVo.setOffset(response.getOffset());
                logVo.setLog(response.getLog());
                logVo.setIsEnd(response.getIsEnd());
                return successResult(logVo);
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            return errorResult(e.getMessage());
        }
    }

}
