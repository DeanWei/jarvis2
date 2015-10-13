/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: muming
 * Create Date: 2015年10月08日 下午3:19:28
 */
package com.mogujie.jarvis.rest.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.protocol.MapEntryProtos;
import com.mogujie.jarvis.protocol.ModifyJobProtos;
import com.mogujie.jarvis.protocol.SubmitJobProtos.DependencyEntry;
import com.mogujie.jarvis.protocol.SubmitJobProtos.RestServerSubmitJobRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.ServerSubmitJobResponse;
import com.mogujie.jarvis.protocol.ModifyJobProtos.RestServerModifyJobRequest;
import com.mogujie.jarvis.protocol.ModifyJobProtos.ServerModifyJobResponse;
import com.mogujie.jarvis.protocol.ModifyDependencyProtos;
import com.mogujie.jarvis.protocol.ModifyDependencyProtos.RestServerModifyDependencyRequest;
import com.mogujie.jarvis.protocol.ModifyDependencyProtos.ServerModifyDependencyResponse;
import com.mogujie.jarvis.protocol.ModifyJobFlagProtos;
import com.mogujie.jarvis.protocol.ModifyJobFlagProtos.RestServerModifyJobFlagRequest;
import com.mogujie.jarvis.protocol.ModifyJobFlagProtos.ServerModifyJobFlagResponse;


import com.mogujie.jarvis.rest.MsgCode;
import com.mogujie.jarvis.rest.RestResult;
import com.mogujie.jarvis.rest.vo.JobVo;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author muming
 */
@Path("job")
public class JobController extends AbstractController {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
    Logger logger = Logger.getLogger(this.getClass());

    /**
     * 提交job任务
     *
     * @throws Exception
     */
    @POST
    @Path("submitJob")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult submitJob(@FormParam("appName") String appName,
                                @FormParam("appKey") String appKey,
                                @FormParam("jobName") String jobName,
                                @FormParam("jobId") Long jobId,
                                @FormParam("cronExpression") String cronExp,
                                @FormParam("dependJobIds") String dependJobIds,
                                @FormParam("user") String user,
                                @FormParam("jobType") String jobType,
                                @FormParam("jobCommand") String jobCommand,
                                //@FormParam("jobContent") String jobContent,
                                @FormParam("groupId") int groupId,
                                @FormParam("rejectRetries") int rejectRetries,
                                @FormParam("rejectInterval") int rejectInterval,
                                @FormParam("failedRetries") int failedRetries,
                                @FormParam("failedInterval") int failedInterval,
                                @FormParam("startTime") String startTime,
                                @FormParam("endTime") String endTime,
                                @FormParam("priority") int priority,
                                @FormParam("parameters") String parameters){

        try {
            // todo , 转换为 list
            List<DependencyEntry> dependEntryList = new ArrayList<DependencyEntry>();
            //不为null且不为空字符串才处理
            if(dependJobIds!=null&&!dependJobIds.equals("")&&!dependJobIds.equals("null")){
                JSONArray dependIdsArr = new JSONArray(dependJobIds);

                for(int i=0;i<dependIdsArr.length();i++){
                    DependencyEntry entry = DependencyEntry.newBuilder().setJobId(dependIdsArr.getLong(i)).build();
                    dependEntryList.add(entry);
                }
            }

            // todo parameters 从json转化为 list
            List<MapEntryProtos.MapEntry> paraList = new ArrayList<MapEntryProtos.MapEntry>();
            //不为null且不为空字符串才处理
            if(parameters!=null&&!parameters.equals("")){
                JSONObject parameterJson = new JSONObject(parameters);
                for(Object key:parameterJson.keySet()){
                    String value=parameterJson.getString((String)key);
                    MapEntryProtos.MapEntry entry=MapEntryProtos.MapEntry.newBuilder().setKey((String)key).setValue(value).build();
                    paraList.add(entry);
                }
            }
            Long startTimeLong=null;
            Long endTimeLong=null;
            if(startTime!=null&&!startTime.equals("")){
                startTimeLong=sdf.parse(startTime).getTime();
            }
            if(endTime!=null&&!endTime.equals("")){
                endTimeLong=sdf.parse(endTime).getTime();
            }

            //构造新增任务请求
            RestServerSubmitJobRequest.Builder builder =RestServerSubmitJobRequest.newBuilder().setAppName(appName).setJobName(jobName).setAppKey(appKey)
                    .setCronExpression(cronExp).addAllDependencyEntry(dependEntryList).setUser(user).setJobType(jobType).setContent(jobCommand)
                    .setGroupId(groupId).setPriority(priority).setFailedRetries(failedRetries).setFailedInterval(failedInterval)
                    .setRejectRetries(rejectRetries).setRejectInterval(rejectInterval)
                    .addAllParameters(paraList);
            if(startTimeLong!=null){
                builder.setStartTime(startTimeLong);
            }
            if(endTimeLong!=null){
                builder.setEndTime(endTimeLong);
            }
            RestServerSubmitJobRequest request = builder.build();

            //发送请求到server尝试新增

            ServerSubmitJobResponse response = (ServerSubmitJobResponse) callActor(AkkaType.server, request);

            //判断是否新增成功
            if (response.getSuccess()) {
                JobVo vo = new JobVo();
                vo.setJobId(response.getJobId());
                return successResult(vo);
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getStackTrace());
            return errorResult(e.getMessage());
        }
    }


    /**
     * 修改job任务
     *
     * @throws Exception
     */
    @POST
    @Path("edit")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult edit(@FormParam("appName") String appName,
                           @FormParam("appKey") String appKey,
                           @FormParam("jobName") String jobName,
                           @FormParam("jobId") Long jobId,
                           @FormParam("cronExpression") String cronExp,
                           @FormParam("dependJobIds") String dependJobIds,
                           @FormParam("user") String user,
                           @FormParam("jobType") String jobType,
                           @FormParam("jobCommand") String jobCommand,
                           //@FormParam("jobContent") String jobContent,
                           @FormParam("groupId") int groupId,
                           @FormParam("rejectRetries") int rejectRetries,
                           @FormParam("rejectInterval") int rejectInterval,
                           @FormParam("failedRetries") int failedRetries,
                           @FormParam("failedInterval") int failedInterval,
                           @FormParam("startTime") String startTime,
                           @FormParam("endTime") String endTime,
                           @FormParam("priority") int priority,
                           @FormParam("parameters") String parameters){
        try {
            logger.info("更新job任务");
//            logger.info(appName);
//            logger.info(appKey);
//            logger.info(jobName);
//            logger.info(jobId);
//            logger.info(cronExp);
//            logger.info(dependJobIds);
//            logger.info(user);
//            logger.info(jobType);
//            logger.info(jobCommand);
//            logger.info(groupId);
//            logger.info(rejectRetries);
//            logger.info(rejectInterval);
//            logger.info(failedRetries);
//            logger.info(failedInterval);
//            logger.info(startTime);
//            logger.info(endTime);
//            logger.info(priority);
//            logger.info(parameters);

            // todo , 转换为 list
            List<ModifyDependencyProtos.DependencyEntry> dependEntryList = new ArrayList<ModifyDependencyProtos.DependencyEntry>();
            // 不为null且不为空字符串才处理
            if(dependJobIds!=null&&!dependJobIds.equals("")){
                JSONArray dependIdsArr = new JSONArray(dependJobIds);
                for(int i=0;i<dependIdsArr.length();i++){
                    ModifyDependencyProtos.DependencyEntry entry = ModifyDependencyProtos.DependencyEntry.newBuilder().setJobId(dependIdsArr.getLong(i)).build();
                    dependEntryList.add(entry);
                }
            }

            // todo parameters 从json转化为 list
            List<MapEntryProtos.MapEntry> paraList = new ArrayList<MapEntryProtos.MapEntry>();
            // 不为null且不为空字符串才处理
            if(parameters!=null&&!parameters.equals("")){
                JSONObject parameterJson = new JSONObject(parameters);
                for(Object key:parameterJson.keySet()){
                    String value=parameterJson.getString((String)key);
                    MapEntryProtos.MapEntry entry=MapEntryProtos.MapEntry.newBuilder().setKey((String)key).setValue(value).build();
                    paraList.add(entry);
                }
            }

            Long startTimeLong=sdf.parse(startTime).getTime();
            Long endTimeLong=sdf.parse(endTime).getTime();

            //构造修改job基本信息请求
            RestServerModifyJobRequest request = RestServerModifyJobRequest.newBuilder().setAppName(appName).setJobName(jobName).setJobId(jobId)
                    .setCronExpression(cronExp).setUser(user).setJobType(jobType).setCommand(jobCommand)
                    .setGroupId(groupId).setPriority(priority).setFailedRetries(failedRetries).setFailedInterval(failedInterval)
                    .setRejectRetries(rejectRetries).setRejectInterval(rejectInterval).setStartTime(startTimeLong).setEndTime(endTimeLong)
                    .addAllParameters(paraList).build();

            //发送信息到server修改job基本信息
            ServerModifyJobResponse response = (ServerModifyJobResponse) callActor(AkkaType.server, request);

            //判断修改基本信息是否成功，修改基本信息成功后才尝试修改依赖
            if (response.getSuccess()) {
                //构造修改job依赖请求
                RestServerModifyDependencyRequest modifyDependencyRequest=RestServerModifyDependencyRequest.newBuilder().setJobId(jobId)
                        .addAllDependencyEntry(dependEntryList).build();
                //发送信息到server修改依赖
                ServerModifyDependencyResponse dependencyResponse = (ServerModifyDependencyResponse)callActor(AkkaType.server, modifyDependencyRequest);
                //修改依赖是否成功
                if(dependencyResponse.getSuccess()){
                    JobVo vo = new JobVo();
                    vo.setJobId(dependencyResponse.getJobId());
                    return successResult(vo);
                }
                else {
                    return errorResult(dependencyResponse.getMessage());
                }
            }
            //修改基本信息出错
            else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getStackTrace());
            return errorResult(e.getMessage());
        }
    }


    /**
     * 删除job任务
     *
     * @throws Exception
     */
    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult delete(@FormParam("appKey") String appKey,
                            @FormParam("appName") String appName,
                            @FormParam("jobId") Long jobId
                            ) throws Exception {
        //构造删除job请求request，状态4代表垃圾箱
        RestServerModifyJobFlagRequest request=RestServerModifyJobFlagRequest.newBuilder().setJobId(jobId)
                .setJobFlag(4).build();
        //发送请求到server尝试常熟
        ServerModifyJobFlagResponse response=(ServerModifyJobFlagResponse) callActor(AkkaType.server,request);

        //判断删除是否成功
        if(response.getSuccess()){
            JobVo vo = new JobVo();
            vo.setJobId(jobId);
            return successResult(vo);
        }
        else{
            return errorResult(response.getMessage());
        }
    }

    /**
     * 删除job任务
     *
     * @throws Exception
     */
    @POST
    @Path("rerun")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult rerun(@FormParam("originJobId") Long originJobId,
                             @FormParam("startTime") String startTime,
                             @FormParam("endTime") String endTime,
                             @FormParam("reRunJobs") String reRunJobs
    ) throws Exception {
        JSONArray reRunJobArr= new JSONArray(reRunJobs);
        boolean hasSelf=false;
        for(int i=0;i<reRunJobArr.length();i++){
            if(originJobId.equals(reRunJobArr.getLong(i))){
                hasSelf=true;
                break;
            }
        }
        if(hasSelf==false){
            reRunJobArr.put(originJobId);
        }

        boolean flag=true;
        JSONObject msg= new JSONObject();
        for(int i=0;i<reRunJobArr.length();i++){
            Long singleOriginId=reRunJobArr.getLong(i);
            //构造新增任务请求
            RestServerSubmitJobRequest request = RestServerSubmitJobRequest.newBuilder().setOriginJobId(singleOriginId).build();
            ServerSubmitJobResponse response = (ServerSubmitJobResponse) callActor(AkkaType.server, request);

            //保存整理而言是否成功，如果某个job重跑失败，则算失败
            flag=flag&&response.getSuccess();
            //判断是否重跑成功
            if (response.getSuccess()) {
                msg.put(singleOriginId.toString(),"success,"+response.getMessage());
            } else {
                msg.put(singleOriginId.toString(),"failed,"+response.getMessage());
            }
        }

        //判断删除是否成功
        if(flag){
            return new RestResult(MsgCode.SUCCESS);
        }
        else{
            return errorResult(msg.toString());
        }
    }

    /**
     * 提交job任务
     *
     * @throws Exception
     */
    @GET
    @Path("test")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult test(
    ) throws Exception {


            JobVo vo = new JobVo();
            vo.setJobId(123456);
            return successResult(vo);

    }


}
