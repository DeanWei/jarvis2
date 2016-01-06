package com.mogujie.jarvis.rest.vo;

import java.util.List;
import java.util.Map;

import com.mogujie.jarvis.core.domain.OperationMode;

/**
 * job返回类
 * @author muming
 */
public class JobEntryVo extends AbstractVo {

    private long jobId;
    private String jobName;
    private String jobType;
    private Integer status;
    private String content;
    private Map<String, Object> params;
    private String appName;
    private Integer workerGroupId;
    private Integer priority;
    private Long activeStartTime;
    private Long activeEndTime;
    private Integer expiredTime;
    private Integer failedAttempts;
    private Integer failedInterval;
    private List<DependencyEntry> dependencyList;
    private List<ScheduleExpressionEntry> scheduleExpressionList;

    public static class DependencyEntry {
        private Integer operatorMode;
        private Long preJobId;
        private Integer commonStrategy;
        private String offsetStrategy;

        public Integer getOperatorMode() {
            return operatorMode;
        }

        public void setOperatorMode(Integer operatorMode) {
            this.operatorMode = operatorMode;
        }

        public Integer getCommonStrategy() {
            return commonStrategy;
        }

        public void setCommonStrategy(Integer commonStrategy) {
            this.commonStrategy = commonStrategy;
        }

        public String getOffsetStrategy() {
            return offsetStrategy;
        }

        public void setOffsetStrategy(String offsetStrategy) {
            this.offsetStrategy = offsetStrategy;
        }

        public Long getPreJobId() {
            return preJobId;
        }

        public void setPreJobId(Long preJobId) {
            this.preJobId = preJobId;
        }
    }

    public static class ScheduleExpressionEntry {
        private OperationMode operatorMode;
        private Integer expressionType;
        private String expression;

        public OperationMode getOperatorMode() {
            return operatorMode;
        }

        public void setOperatorMode(OperationMode operatorMode) {
            this.operatorMode = operatorMode;
        }

        public Integer getExpressionType() {
            return expressionType;
        }

        public void setExpressionType(Integer expressionType) {
            this.expressionType = expressionType;
        }

        public String getExpression() {
            return expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }
    }

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Integer getWorkerGroupId() {
        return workerGroupId;
    }

    public void setWorkerGroupId(Integer workerGroupId) {
        this.workerGroupId = workerGroupId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Long getActiveStartTime() {
        return activeStartTime;
    }

    public void setActiveStartTime(Long activeStartTime) {
        this.activeStartTime = activeStartTime;
    }

    public Long getActiveEndTime() {
        return activeEndTime;
    }

    public void setActiveEndTime(Long activeEndTime) {
        this.activeEndTime = activeEndTime;
    }

    public Integer getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Integer expiredTime) {
        this.expiredTime = expiredTime;
    }

    public Integer getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(Integer failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public Integer getFailedInterval() {
        return failedInterval;
    }

    public void setFailedInterval(Integer failedInterval) {
        this.failedInterval = failedInterval;
    }

    public List<DependencyEntry> getDependencyList() {
        return dependencyList;
    }

    public void setDependencyList(List<DependencyEntry> dependencyList) {
        this.dependencyList = dependencyList;
    }

    public List<ScheduleExpressionEntry> getScheduleExpressionList() {
        return scheduleExpressionList;
    }

    public void setScheduleExpressionList(List<ScheduleExpressionEntry> scheduleExpressionList) {
        this.scheduleExpressionList = scheduleExpressionList;
    }

    public String getAppName(String defaultValue) {
        return (appName != null) ? appName : defaultValue;
    }

    public Long getActiveStartTime(Long defaultValue) {
        return (activeStartTime != null) ? activeStartTime : defaultValue;
    }

    public Long getActiveEndTime(Long defaultValue) {
        return (activeEndTime != null) ? activeEndTime : defaultValue;
    }

    public Integer getPriority(Integer defaultValue) {
        return (priority != null) ? priority : defaultValue;
    }

    public Integer getExpiredTime(Integer defaultValue) {
        return (expiredTime != null) ? expiredTime : defaultValue;
    }

    public Integer getFailedAttempts(Integer defaultValue) {
        return (failedAttempts != null) ? failedAttempts : defaultValue;
    }

    public Integer getFailedInterval(Integer defaultValue) {
        return (failedInterval != null) ? failedInterval : defaultValue;
    }

}
