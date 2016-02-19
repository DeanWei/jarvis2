package com.mogujie.jarvis.web.entity.vo;

import com.mogujie.jarvis.dto.generate.Plan;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hejian on 15/10/21.
 */
public class PlanVo extends Plan {

    private String jobName;
    private String jobType;
    private String submitUser;
    private Integer priority;
    private Integer appId;
    private String appName;
    private String workerGroupId;
    private String workerGroupName;
    private Integer bizGroupId;
    private String bizGroupName;

    private List<Object> taskStatus =new ArrayList<>();
    private int taskSize;

    private Date scheduleTimeFirst;

    public String getJobName() {
        return jobName;
    }

    public PlanVo setJobName(String jobName) {
        this.jobName = jobName;
        return this;
    }

    public String getJobType() {
        return jobType;
    }

    public PlanVo setJobType(String jobType) {
        this.jobType = jobType;
        return this;
    }

    public String getSubmitUser() {
        return submitUser;
    }

    public PlanVo setSubmitUser(String submitUser) {
        this.submitUser = submitUser;
        return this;
    }

    public Integer getPriority() {
        return priority;
    }

    public PlanVo setPriority(Integer priority) {
        this.priority = priority;
        return this;
    }

    public Integer getAppId() {
        return appId;
    }

    public PlanVo setAppId(Integer appId) {
        this.appId = appId;
        return this;
    }

    public String getAppName() {
        return appName;
    }

    public PlanVo setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public String getWorkerGroupId() {
        return workerGroupId;
    }

    public PlanVo setWorkerGroupId(String workerGroupId) {
        this.workerGroupId = workerGroupId;
        return this;
    }

    public String getWorkerGroupName() {
        return workerGroupName;
    }

    public PlanVo setWorkerGroupName(String workerGroupName) {
        this.workerGroupName = workerGroupName;
        return this;
    }

    public Integer getBizGroupId() {
        return bizGroupId;
    }

    public PlanVo setBizGroupId(Integer bizGroupId) {
        this.bizGroupId = bizGroupId;
        return this;
    }

    public String getBizGroupName() {
        return bizGroupName;
    }

    public PlanVo setBizGroupName(String bizGroupName) {
        this.bizGroupName = bizGroupName;
        return this;
    }

    public List<Object> getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(List<Object> taskStatus) {
        this.taskStatus = taskStatus;
    }

    public int getTaskSize() {
        return taskSize;
    }

    public PlanVo setTaskSize(int taskSize) {
        this.taskSize = taskSize;
        return this;
    }

    public Date getScheduleTimeFirst() {
        return scheduleTimeFirst;
    }

    public PlanVo setScheduleTimeFirst(Date scheduleTimeFirst) {
        this.scheduleTimeFirst = scheduleTimeFirst;
        return this;
    }
}
