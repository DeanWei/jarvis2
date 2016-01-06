/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月15日 下午2:35:11
 */
package com.mogujie.jarvis.server.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.mybatis.guice.transactional.Transactional;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.core.expression.CronExpression;
import com.mogujie.jarvis.core.expression.DefaultDependencyStrategyExpression;
import com.mogujie.jarvis.core.expression.DependencyExpression;
import com.mogujie.jarvis.core.expression.DependencyStrategyExpression;
import com.mogujie.jarvis.core.expression.FixedDelayExpression;
import com.mogujie.jarvis.core.expression.FixedRateExpression;
import com.mogujie.jarvis.core.expression.ISO8601Expression;
import com.mogujie.jarvis.core.expression.ScheduleExpression;
import com.mogujie.jarvis.core.expression.ScheduleExpressionType;
import com.mogujie.jarvis.core.expression.TimeOffsetExpression;
import com.mogujie.jarvis.dao.generate.JobDependMapper;
import com.mogujie.jarvis.dao.generate.JobMapper;
import com.mogujie.jarvis.dao.generate.JobScheduleExpressionMapper;
import com.mogujie.jarvis.dto.generate.Job;
import com.mogujie.jarvis.dto.generate.JobDepend;
import com.mogujie.jarvis.dto.generate.JobDependExample;
import com.mogujie.jarvis.dto.generate.JobDependKey;
import com.mogujie.jarvis.dto.generate.JobExample;
import com.mogujie.jarvis.dto.generate.JobScheduleExpression;
import com.mogujie.jarvis.dto.generate.JobScheduleExpressionExample;
import com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry;
import com.mogujie.jarvis.server.domain.JobDependencyEntry;
import com.mogujie.jarvis.server.domain.JobEntry;

/**
 * @author wuya
 */
@Singleton
public class JobService {

    private static final Logger LOGGER = LogManager.getLogger();
    private Map<Long, JobEntry> metaStore = Maps.newConcurrentMap();

    @Inject
    private JobMapper jobMapper;

    @Inject
    private JobScheduleExpressionMapper jobScheduleExpressionMapper;

    @Inject
    private JobDependMapper jobDependMapper;

    @Inject
    private void init() {
        loadMetaDataFromDB();
        LOGGER.info("jobService loadMetaDataFromDB finished.");
    }

    public Map<Long, JobEntry> getMetaStore() {
        return metaStore;
    }

    //------------------------  job信息处理 -------------------------------------

    public JobEntry get(long jobId) {
        return metaStore.get(jobId);
    }

    public List<Job> getNotDeletedJobs() {
        JobExample example = new JobExample();
        example.createCriteria().andStatusNotEqualTo(JobStatus.DELETED.getValue());
        List<Job> jobs = jobMapper.selectByExampleWithBLOBs(example);
        if (jobs == null) {
            jobs = new ArrayList<>();
        }
        return jobs;
    }

    public boolean isActive(long jobId) {
        Job job = metaStore.get(jobId).getJob();
        Date startDate = job.getActiveStartDate();
        Date endDate = job.getActiveEndDate();
        Date now = DateTime.now().toDate();
        if ((startDate == null || now.after(startDate)) && (endDate == null || now.before(endDate))) {
            return true;
        } else {
            return false;
        }
    }

    public long insertJob(Job record) {
        // 1. insert to DB
        jobMapper.insertSelective(record);
        long jobId = record.getJobId();

        // 2. insert to cache
        Job newRecord = jobMapper.selectByPrimaryKey(jobId);
        JobEntry jobEntry = new JobEntry(newRecord, Maps.newHashMap(), Maps.newHashMap());
        metaStore.put(jobId, jobEntry);

        return jobId;
    }

    public void updateJob(Job record) {
        // 1. update to DB
        jobMapper.updateByPrimaryKeySelective(record);

        // 2. update to cache
        long jobId = record.getJobId();
        JobEntry jobEntry = metaStore.get(jobId);
        if (jobEntry != null) {
            Job newRecord = jobMapper.selectByPrimaryKey(jobId);
            jobEntry.setJob(newRecord);
        }
    }

    public void updateStatus(long jobId, String user, int status) {
        Job record = jobMapper.selectByPrimaryKey(jobId);
        record.setStatus(status);
        record.setUpdateUser(user);
        record.setUpdateTime(DateTime.now().toDate());
        jobMapper.updateByPrimaryKey(record);

        JobEntry jobEntry = metaStore.get(jobId);
        if (jobEntry != null) {
            jobEntry.updateJobStatus(status);
        }
    }

    public void deleteJob(long jobId) {
        jobMapper.deleteByPrimaryKey(jobId);
        metaStore.remove(jobId);
    }

    //------------------------  job计划表达式——处理 -------------------------------------

    /**
     * 获取job的计划表达式
     *
     * @param jobId
     * @return
     */
    public JobScheduleExpression getScheduleExpressionByJobId(long jobId) {
        JobScheduleExpressionExample example = new JobScheduleExpressionExample();
        example.createCriteria().andJobIdEqualTo(jobId);
        List<JobScheduleExpression> records = jobScheduleExpressionMapper.selectByExample(example);
        JobScheduleExpression record = null;
        if (records != null && !records.isEmpty()) {
            record = records.get(0);
        }
        return record;
    }

    /**
     * 插入job的计划表达式
     *
     * @param jobId
     * @param entry
     */
    public void insertScheduleExpression(long jobId, ScheduleExpressionEntry entry) {
        // 1. insert to DB
        JobScheduleExpression record = new JobScheduleExpression();
        record.setJobId(jobId);
        record.setExpressionType(entry.getExpressionType());
        record.setExpression(entry.getScheduleExpression());
        Date now = DateTime.now().toDate();
        record.setCreateTime(now);
        record.setUpdateTime(now);
        jobScheduleExpressionMapper.insert(record);

        // 2. insert to cache
        ScheduleExpressionType expressionType = ScheduleExpressionType.parseValue(record.getExpressionType());
        String expression = record.getExpression();
        ScheduleExpression scheduleExpression = getScheduleExpression(expressionType, expression);
        JobEntry jobEntry = metaStore.get(jobId);
        jobEntry.addScheduleExpression(entry.getExpressionId(), scheduleExpression);
    }

    public void deleteScheduleExpression(long jobId, long expressionId) {
        jobScheduleExpressionMapper.deleteByPrimaryKey(expressionId);
        get(jobId).removeScheduleExpression(expressionId);
   }

    /**
     * 更新job的计划表达式
     *
     * @param jobId
     * @param entry
     */
    public void updateScheduleExpression(long jobId, ScheduleExpressionEntry entry) {
        // 1. update to DB
        long expressionId = entry.getExpressionId();
        JobScheduleExpression record = jobScheduleExpressionMapper.selectByPrimaryKey(expressionId);
        record.setExpressionType(entry.getExpressionType());
        record.setExpression(entry.getScheduleExpression());
        record.setUpdateTime(DateTime.now().toDate());
        jobScheduleExpressionMapper.updateByPrimaryKey(record);

        // 2. update to cache
        ScheduleExpressionType expressionType = ScheduleExpressionType.parseValue(record.getExpressionType());
        String expression = record.getExpression();
        ScheduleExpression scheduleExpression = getScheduleExpression(expressionType, expression);
        JobEntry jobEntry = metaStore.get(jobId);
        jobEntry.updateScheduleExpression(entry.getExpressionId(), scheduleExpression);
    }

    public void deleteScheduleExpressionByJobId(long jobId) {
        JobScheduleExpressionExample example = new JobScheduleExpressionExample();
        example.createCriteria().andJobIdEqualTo(jobId);
        jobScheduleExpressionMapper.deleteByExample(example);

        JobEntry jobEntry = metaStore.get(jobId);
        jobEntry.clearScheduleExpressions();
    }

    //------------------------  job依赖处理 -------------------------------------

    public JobDepend getJobDepend(JobDependKey key) {
        return jobDependMapper.selectByPrimaryKey(key);
    }

    public void insertJobDepend(JobDepend record) {
        jobDependMapper.insertSelective(record);

        JobDependencyEntry jobDependencyEntry = getJobDependencyEntry(record);
        JobEntry jobEntry = metaStore.get(record.getJobId());
        jobEntry.addDependency(record.getPreJobId(), jobDependencyEntry);
    }

    public void updateJobDepend(JobDepend record) {
        jobDependMapper.updateByPrimaryKey(record);

        JobDependencyEntry jobDependencyEntry = getJobDependencyEntry(record);
        JobEntry jobEntry = metaStore.get(record.getJobId());
        jobEntry.updateDependency(record.getPreJobId(), jobDependencyEntry);
    }

    public void deleteJobDepend(long jobId, long preJobId) {
        JobDependKey key = new JobDependKey();
        key.setJobId(jobId);
        key.setPreJobId(preJobId);
        jobDependMapper.deleteByPrimaryKey(key);

        JobEntry jobEntry = metaStore.get(jobId);
        if (jobEntry != null) {
            jobEntry.removeDependency(preJobId);
        }
    }

    public void deleteJobDependByPreJob(long preJobId) {
        JobDependExample jobDependExample = new JobDependExample();
        jobDependExample.createCriteria().andPreJobIdEqualTo(preJobId);
        List<JobDepend> jobDependList = jobDependMapper.selectByExample(jobDependExample);
        jobDependMapper.deleteByExample(jobDependExample);

        if (jobDependList != null) {
            for (JobDepend jobDepend : jobDependList) {
                JobEntry jobEntry = metaStore.get(jobDepend.getJobId());
                if (jobEntry != null) {
                    jobEntry.removeDependency(preJobId);
                }
            }
        }
    }

    //------------------------  载入数据 -------------------------------------

    /**
     * 读取metaData
     */
    @Transactional
    private void loadMetaDataFromDB() {
        List<Job> jobs = getNotDeletedJobs();
        List<JobScheduleExpression> scheduleExpressions = jobScheduleExpressionMapper.selectByExample(new JobScheduleExpressionExample());
        Map<Long, Map<Long, ScheduleExpression>> scheduleExpressionMap = Maps.newHashMap();
        for (JobScheduleExpression jobScheduleExpression : scheduleExpressions) {
            long jobId = jobScheduleExpression.getJobId();
            long expressionId = jobScheduleExpression.getId();
            ScheduleExpressionType expressionType;
            try {
                expressionType = ScheduleExpressionType.parseValue(jobScheduleExpression.getExpressionType());
            } catch (Exception ex) {
                LOGGER.warn("ExpressionType is undefined. id={};type={}", jobId, jobScheduleExpression.getExpressionType());
                continue;
            }
            String expression = jobScheduleExpression.getExpression();
            ScheduleExpression scheduleExpression = getScheduleExpression(expressionType, expression);
            if (scheduleExpression == null || !scheduleExpression.isValid()) {
                LOGGER.warn("expression value is invalid. id={};value={}", jobId, expression);
                continue;
            }

            if (scheduleExpressionMap.containsKey(jobId)) {
                Map<Long, ScheduleExpression> expressionMap = scheduleExpressionMap.get(jobId);
                expressionMap.put(expressionId, scheduleExpression);
            } else {
                Map<Long, ScheduleExpression> expressionMap = Maps.newHashMap();
                expressionMap.put(expressionId, scheduleExpression);
                scheduleExpressionMap.put(jobId, expressionMap);
            }
        }

        List<JobDepend> jobDepends = jobDependMapper.selectByExample(new JobDependExample());
        Multimap<Long, JobDepend> jobDependMap = ArrayListMultimap.create();
        for (JobDepend jobDepend : jobDepends) {
            jobDependMap.put(jobDepend.getJobId(), jobDepend);
        }

        for (Job job : jobs) {
            long jobId = job.getJobId();
            Map<Long, ScheduleExpression> expressionMap = scheduleExpressionMap.get(jobId);
            Map<Long, JobDependencyEntry> dependencies = Maps.newHashMap();
            Collection<JobDepend> jobDependsCollection = jobDependMap.get(jobId);
            if (jobDependsCollection != null && jobDependsCollection.size() > 0) {
                for (JobDepend jobDepend : jobDependsCollection) {
                    JobDependencyEntry jobDependencyEntry = getJobDependencyEntry(jobDepend);
                    if (jobDependencyEntry != null) {
                        dependencies.put(jobDepend.getPreJobId(), jobDependencyEntry);
                    }
                }
            }

            // 初始化 JobMetaStore
            metaStore.put(job.getJobId(), new JobEntry(job, expressionMap, dependencies));
        }
    }

    private JobDependencyEntry getJobDependencyEntry(JobDepend jobDepend) {
        long jobId = jobDepend.getJobId();
        String offsetStrategy = jobDepend.getOffsetStrategy();
        if (offsetStrategy.isEmpty()) {
            offsetStrategy = null;
        }

        String commonStrategyStr = null;
        Integer commonStrategy = jobDepend.getCommonStrategy();
        if (commonStrategy == null) {
            commonStrategyStr = "*";
        } else {
            switch (commonStrategy) {
                case 1:
                    commonStrategyStr = "L(1)";
                    break;
                case 2:
                    commonStrategyStr = "+";
                    break;
                default:
                    commonStrategyStr = "*";
                    break;
            }
        }

        // 检查依赖表达式是否有效
        DependencyExpression dependencyExpression = null;
        if (offsetStrategy != null) {
            dependencyExpression = new TimeOffsetExpression(offsetStrategy);
            if (!dependencyExpression.isValid()) {
                LOGGER.warn("dependency expression is invalid. id={}; value={}", jobId, dependencyExpression.toString());
                return null;
            }
        }

        // 检查依赖策略表达式是否有效
        DependencyStrategyExpression dependencyStrategyExpression = new DefaultDependencyStrategyExpression(commonStrategyStr);
        if (!dependencyStrategyExpression.isValid()) {
            LOGGER.warn("dependency strategy is invalid. id={}; value={}", jobId, dependencyStrategyExpression.toString());
            return null;
        }

        JobDependencyEntry jobDependencyEntry = new JobDependencyEntry(dependencyExpression, dependencyStrategyExpression);
        return jobDependencyEntry;
    }

    /**
     * @return
     */
    private ScheduleExpression getScheduleExpression(ScheduleExpressionType expressionType, String expression) {

        ScheduleExpression scheduleExpression = null;
        if (expressionType == ScheduleExpressionType.CRON) {
            scheduleExpression = new CronExpression(expression);
        } else if (expressionType == ScheduleExpressionType.FIXED_RATE) {
            scheduleExpression = new FixedRateExpression(expression);
        } else if (expressionType == ScheduleExpressionType.FIXED_DELAY) {
            scheduleExpression = new FixedDelayExpression(expression);
        } else if (expressionType == ScheduleExpressionType.ISO8601) {
            scheduleExpression = new ISO8601Expression(expression);
        }

        return scheduleExpression;
    }


    @VisibleForTesting
    public void deleteJobAndRelation(long jobId) {
        deleteJob(jobId);
        deleteJobDependByPreJob(jobId);
        deleteScheduleExpressionByJobId(jobId);
    }

}