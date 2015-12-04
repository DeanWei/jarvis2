package com.mogujie.jarvis.rest;

import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.rest.vo.JobVo;
import com.mogujie.jarvis.rest.vo.TaskEntryVo;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by muming on 15/12/1.
 */
public class TestRestTask {

    private String baseUrl = "http://127.0.0.1:8080";

    @Test
    public void taskSubmit() throws UnirestException {

//        private String taskName;
//        private String user;
//        private String taskType;
//        private String content;
//        private int groupId;
//        private Integer priority;
//        private Integer rejectRetries;
//        private Integer rejectInterval;
//        private Integer failedRetries;
//        private Integer failedInterval;
//        private Map<String,Object> params;

        TaskEntryVo task = new TaskEntryVo();
        task.setTaskName("mmTest");
        task.setUser("muming");
        task.setTaskType("dummy");
        task.setContent("welcome to dummy");
        task.setGroupId(1);

        //任务参数
        Map<String,Object> jobPrams = new HashMap<>();
        jobPrams.put("name","muming");
        jobPrams.put("age", 18);
        jobPrams.put("isMail", false);
        task.setParams(jobPrams);

        // 任务参数
        String paramsJson = JsonHelper.toJson(task, TaskEntryVo.class);

        HttpResponse<String> jsonResponse = Unirest.post(baseUrl + "/api/task/submit").field("appName", "jarvis-web").field("appToken", "123")
                .field("user", "muming").field("parameters", paramsJson).asString();

        Type restType = new TypeToken<RestResult<JobVo>>() {
        }.getType();

        Assert.assertEquals(jsonResponse.getStatus(), 200);
        RestResult<?> result = JsonHelper.fromJson(jsonResponse.getBody(), restType);
        Assert.assertEquals(result.getCode(), 0);
    }

}