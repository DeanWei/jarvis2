/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:50:07
 */

package com.mogujie.jarvis.server.scheduler.dag;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;
import org.jgrapht.graph.DefaultEdge;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.dao.JobMapper;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.dto.Plan;
import com.mogujie.jarvis.server.domain.JobKey;
import com.mogujie.jarvis.server.domain.ModifyDependEntry;
import com.mogujie.jarvis.server.domain.ModifyJobEntry;
import com.mogujie.jarvis.server.domain.ModifyJobType;
import com.mogujie.jarvis.server.domain.ModifyOperation;
import com.mogujie.jarvis.server.scheduler.JobScheduleException;
import com.mogujie.jarvis.server.scheduler.Scheduler;
import com.mogujie.jarvis.server.scheduler.SchedulerUtil;
import com.mogujie.jarvis.server.scheduler.dag.checker.DAGDependChecker;
import com.mogujie.jarvis.server.scheduler.dag.strategy.AbstractOffsetStrategy;
import com.mogujie.jarvis.server.scheduler.dag.strategy.CommonStrategy;
import com.mogujie.jarvis.server.scheduler.dag.strategy.OffsetStrategyFactory;
import com.mogujie.jarvis.server.scheduler.event.FailedEvent;
import com.mogujie.jarvis.server.scheduler.event.ModifyJobFlagsEvent;
import com.mogujie.jarvis.server.scheduler.event.StartEvent;
import com.mogujie.jarvis.server.scheduler.event.StopEvent;
import com.mogujie.jarvis.server.scheduler.event.SuccessEvent;
import com.mogujie.jarvis.server.scheduler.event.TimeReadyEvent;
import com.mogujie.jarvis.server.scheduler.task.TaskScheduler;
import com.mogujie.jarvis.server.service.CrontabService;
import com.mogujie.jarvis.server.service.JobDependService;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.service.PlanService;

/**
 * Scheduler used to handle dependency based job.
 *
 * @author guangming
 *
 */
@Repository
public class DAGScheduler extends Scheduler {
    @Autowired
    private JobService jobService;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private PlanService planService;

    @Autowired
    private JobDependService jobDependService;

    @Autowired
    private CrontabService cronService;

    @Autowired
    private TaskScheduler taskScheduler;

    private Map<JobKey, DAGJob> waitingTable = new ConcurrentHashMap<JobKey, DAGJob>();
    private DirectedAcyclicGraph<DAGJob, DefaultEdge> dag = new DirectedAcyclicGraph<DAGJob, DefaultEdge>(DefaultEdge.class);

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    @Transactional
    protected void init() {
        getSchedulerController().register(this);

        // load plan from DB
        List<Plan> plans = planService.getAllPlans();
        for (Plan plan : plans) {
            long jobId = plan.getJobId();
            Date planDate = plan.getPlanDate();
            DateTime planDateTime = new DateTime(planDate);
            long version = Long.parseLong(planDateTime.toString("yyyyMMdd"));
            Set<Long> preJobIds = jobDependService.getDependIds(jobId);
            Set<JobKey> dependencies = Sets.newHashSet();
            for (long preJobId : preJobIds) {
                Plan preJobVersion = planService.getTodayPlan(preJobId, planDate);
                if (preJobVersion != null) {
                    dependencies.add(new JobKey(preJobId, version));
                }
            }

            Job job = jobMapper.selectByPrimaryKey(jobId);
            Integer fixedDelay = job.getFixedDelay();
            int cycleFlag = (fixedDelay != null && fixedDelay > 0) ? 1 : 0;
            int dependFlag = (cronService.getPositiveCrontab(jobId) != null) ? 1 : 0;
            int timeFlag = (!dependencies.isEmpty()) ? 1 : 0;
            DAGJobType type = SchedulerUtil.getDAGJobType(cycleFlag, dependFlag, timeFlag);
            JobFlag jobFlag = JobFlag.getInstance(job.getJobFlag());
            if (jobFlag.equals(JobFlag.ENABLE) && job.getActiveEndDate().before(planDate)) {
                jobFlag = JobFlag.EXPIRED;
            }

            try {
                JobKey key = new JobKey(jobId, version);
                addJob(key, new DAGJob(key, type, jobFlag), dependencies);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    protected void destroy() {
        clear();
        getSchedulerController().unregister(this);
    }

    @Override
    public void handleStartEvent(StartEvent event) {

    }

    @Override
    public void handleStopEvent(StopEvent event) {

    }

    /**
     * Add job
     *
     * @param jobId
     * @param dagJob
     * @param dependencies set of dependency jobId
     * @throws JobScheduleException
     */
    public void addJob(JobKey jobKey, DAGJob dagJob, Set<JobKey> dependencies) throws JobScheduleException {
        if (waitingTable.get(jobKey) == null) {
            dag.addVertex(dagJob);
            LOGGER.debug("add DAGJob {} to graph successfully.", dagJob.toString());
            if (dependencies != null) {
                for (JobKey key : dependencies) {
                    DAGJob parent = waitingTable.get(key);
                    if (parent != null) {
                        try {
                            // 过滤自依赖
                            if (parent.getJobKey() != jobKey) {
                                dag.addDagEdge(parent, dagJob);
                                LOGGER.debug("add dependency successfully, parent is {}, child is {}",
                                        parent.getJobKey(), dagJob.getJobKey());
                            }
                        } catch (CycleFoundException e) {
                            LOGGER.error(e);
                            dag.removeVertex(dagJob);
                            LOGGER.debug("rollback successfully, remove DAGJob {} from graph", dagJob.toString());
                            throw new JobScheduleException(e);
                        }
                    }
                }
            }
            waitingTable.put(jobKey, dagJob);
            LOGGER.info("add DAGJob {} to DAGScheduler successfully.", dagJob.toString());
        }
    }

    /**
     * Remove job
     *
     * @param jobId
     * @throws JobScheduleException
     */
    public void removeJob(JobKey jobKey) throws JobScheduleException {
        if (waitingTable.containsKey(jobKey)) {
            DAGJob dagJob = waitingTable.get(jobKey);
            dagJob.resetDependStatus();
            dag.removeVertex(dagJob);
            waitingTable.remove(jobKey);
            LOGGER.info("remove DAGJob {} from DAGScheduler successfully.", jobKey);
        }
    }

    @VisibleForTesting
    protected void clear() {
        waitingTable.clear();

        Set<DAGJob> allJobs = dag.vertexSet();
        if (allJobs != null) {
            List<DAGJob> tmpJobs = new ArrayList<DAGJob>();
            tmpJobs.addAll(dag.vertexSet());
            dag.removeAllVertices(tmpJobs);
        }
    }

    /**
     * modify job flag
     *
     * @param jobId
     * @param jobFlag
     * @throws JobScheduleException
     */
    public void modifyJobFlag(JobKey jobKey, JobFlag jobFlag) throws JobScheduleException {
        DAGJob dagJob = waitingTable.get(jobKey);
        List<DAGJob> children = new ArrayList<DAGJob>();
        if (dagJob != null) {
            children = getChildren(dagJob);
        }

        if (jobFlag.equals(JobFlag.DELETED)) {
            if (dagJob != null) {
                removeJob(dagJob);
                LOGGER.info("remove DAGJob {} from DAGScheduler successfully.", dagJob.getJobKey());
            }
        } else {
            if (dagJob != null) {
                JobFlag oldFlag = dagJob.getJobFlag();
                dagJob.setJobFlag(jobFlag);
                LOGGER.info("moidfy job flag from {} to {}.", oldFlag, jobFlag);
            }
        }

        if (children != null) {
            // submit job if pass dependency check
            for (DAGJob child : children) {
                submitJobWithCheck(child);
            }
        }
    }

    @VisibleForTesting
    protected void removeJob(DAGJob dagJob) {
        if (dagJob != null) {
            waitingTable.remove(dagJob.getJobKey());
            dag.removeVertex(dagJob);
        }
    }

    /**
     * modify DAG job type
     *
     * @param jobId
     * @param modifyJobMap Map of ModifyJobType(key) and ModifyJobEntry(value)
     * @throws JobScheduleException
     */
    public void modifyDAGJobType(JobKey jobKey, Map<ModifyJobType, ModifyJobEntry> modifyJobMap)
            throws JobScheduleException {
        // update dag job type
        DAGJob dagJob = waitingTable.get(jobKey);
        if (dagJob != null) {
            DAGJobType oldType = dagJob.getType();
            if (modifyJobMap.containsKey(ModifyJobType.CRON)) {
                ModifyJobEntry entry = modifyJobMap.get(ModifyJobType.CRON);
                ModifyOperation operation = entry.getOperation();
                if (operation.equals(ModifyOperation.DEL)) {
                    dagJob.updateJobTypeByTimeFlag(false);
                    LOGGER.info("DAGJob {} remove time flag, type from {} to {}",
                            dagJob.getJobKey(), oldType, dagJob.getType());
                } else if (operation.equals(ModifyOperation.ADD)) {
                    dagJob.updateJobTypeByTimeFlag(true);
                    LOGGER.info("DAGJob {} add time flag, type from {} to {}",
                            dagJob.getJobKey(), oldType, dagJob.getType());
                }
            }
            if (modifyJobMap.containsKey(ModifyJobType.CYCLE)) {
                ModifyJobEntry entry = modifyJobMap.get(ModifyJobType.CYCLE);
                ModifyOperation operation = entry.getOperation();
                if (operation.equals(ModifyOperation.DEL)) {
                    dagJob.updateJobTypeByCycleFlag(false);
                    LOGGER.info("DAGJob {} remove cycle flag, type from {} to {}",
                            dagJob.getJobKey(), oldType, dagJob.getType());
                } else if (operation.equals(ModifyOperation.ADD)) {
                    dagJob.updateJobTypeByCycleFlag(true);
                    LOGGER.info("DAGJob {} add cycle flag, type from {} to {}",
                            dagJob.getJobKey(), oldType, dagJob.getType());
                }
            }
            submitJobWithCheck(dagJob);
        }
    }

    /**
     * modify DAG job dependency
     *
     * @param jobId
     * @param dependEntries List of ModifyDependEntry
     */
    public void modifyDependency(JobKey jobKey, List<ModifyDependEntry> dependEntries) throws CycleFoundException {
        for (ModifyDependEntry entry : dependEntries) {
            JobKey preJobKey = entry.getPreJobKey();
            if (entry.getOperation().equals(ModifyOperation.ADD)) {
                addDependency(preJobKey, jobKey);
                LOGGER.info("add dependency successfully, parent {}, child {}", preJobKey, jobKey);
            } else if (entry.getOperation().equals(ModifyOperation.DEL)) {
                removeDependency(preJobKey, jobKey);
                LOGGER.info("remove dependency successfully, parent {}, child {}", preJobKey, jobKey);
            } else if (entry.getOperation().equals(ModifyOperation.MODIFY)) {
                modifyDependency(preJobKey, jobKey, entry.getCommonStrategy(), entry.getOffsetStrategy());
                LOGGER.info("modify dependency strategy, new common strategy is {}, new offset Strategy is {}",
                        entry.getCommonStrategy(), entry.getOffsetStrategy());
            }
        }

        // update dag job type
        DAGJob dagJob = waitingTable.get(jobKey);
        if (dagJob != null) {
            DAGJobType oldType = dagJob.getType();
            boolean hasDepend = (!getParents(dagJob).isEmpty());
            dagJob.updateJobTypeByDependFlag(hasDepend);
            if (!oldType.equals(dagJob.getType())) {
                LOGGER.info("moidfy DAGJob type from {} to {}", oldType, dagJob.getType());
            }
            submitJobWithCheck(dagJob);
        }
    }

    @VisibleForTesting
    protected void addDependency(JobKey parentKey, JobKey childKey) throws CycleFoundException {
        DAGJob parent = waitingTable.get(parentKey);
        DAGJob child = waitingTable.get(childKey);
        if (parent != null && child != null) {
            dag.addDagEdge(parent, child);
        }
    }

    @VisibleForTesting
    protected void removeDependency(JobKey parentKey, JobKey childKey) {
        DAGJob parent = waitingTable.get(parentKey);
        DAGJob child = waitingTable.get(childKey);
        if (parent != null && child != null) {
            dag.removeEdge(parent, child);
        }
    }

    protected void modifyDependency(JobKey parentKey, JobKey childKey, int commonStrategyValue,
            String offsetStrategyValue) {
        DAGJob parent = waitingTable.get(parentKey);
        DAGJob child = waitingTable.get(childKey);
        if (parent != null && child != null) {
            DAGDependChecker checker = child.getDependChecker();
            CommonStrategy commonStrategy = CommonStrategy.getInstance(commonStrategyValue);
            checker.updateCommonStrategy(parentKey, commonStrategy);
            Pair<AbstractOffsetStrategy, Integer> offsetStrategyPair = OffsetStrategyFactory.create(offsetStrategyValue);
            if (offsetStrategyPair != null) {
                AbstractOffsetStrategy offsetStrategy = offsetStrategyPair.getFirst();
                checker.updateOffsetStrategy(parentKey, offsetStrategy);
            }
        }
    }

    /**
     * get dependent parent
     *
     * @param jobId
     * @return List of parents'pair with jobid and JobFlag
     */
    public List<Pair<Long, JobFlag>> getParents(JobKey jobKey) {
        List<Pair<Long, JobFlag>> parentJobPairs = new ArrayList<Pair<Long, JobFlag>>();
        DAGJob dagJob = waitingTable.get(jobKey);
        if (dagJob != null) {
            List<DAGJob> parents = getParents(dagJob);
            if (parents != null) {
                for (DAGJob parent : parents) {
                    Pair<Long, JobFlag> jobPair = new Pair<Long, JobFlag>(parent.getJobKey().getJobId(),
                            parent.getJobFlag());
                    parentJobPairs.add(jobPair);
                }
            }
        }

        return parentJobPairs;
    }

    /**
     * get subsequent child
     *
     * @param jobId
     * @return List of children'pair with jobid and JobFlag
     */
    public List<Pair<Long, JobFlag>> getChildren(JobKey jobKey) {
        List<Pair<Long, JobFlag>> childJobPairs = new ArrayList<Pair<Long, JobFlag>>();
        DAGJob dagJob = waitingTable.get(jobKey);
        if (dagJob != null) {
            List<DAGJob> children = getChildren(dagJob);
            if (children != null) {
                for (DAGJob child : children) {
                    Pair<Long, JobFlag> jobPair = new Pair<Long, JobFlag>(child.getJobKey().getJobId(),
                            child.getJobFlag());
                    childJobPairs.add(jobPair);
                }
            }
        }

        return childJobPairs;
    }

    @Subscribe
    public void handleTimeReadyEvent(TimeReadyEvent e) {
        JobKey key = e.getJobKey();
        DAGJob dagJob = waitingTable.get(key);
        if (dagJob != null) {
            if (!(dagJob.getType().implies(DAGJobType.TIME))) {
                LOGGER.warn("DAGJob {} doesn't imply TIME type , auto fix to add TIME type.", key);
                dagJob.updateJobTypeByTimeFlag(true);
            }
            // 更新时间标识
            dagJob.setTimeReadyFlag();
            LOGGER.debug("DAGJob {} time ready", dagJob.getJobKey());
            // 如果通过依赖检查，提交给taskScheduler，并重置自己的依赖状态
            submitJobWithCheck(dagJob);
        }
    }

    @Subscribe
    public void handleModifyJobFlagsEvent(ModifyJobFlagsEvent e) {
        List<Long> jobIds = e.getJobIds();
        JobFlag newFlag = e.getNewFlag();
        for (long jobId : jobIds) {
            DAGJob dagJob = waitingTable.get(jobId);
            if (dagJob != null) {
                if (newFlag.equals(JobFlag.DELETED)) {
                    removeJob(dagJob);
                } else {
                    dagJob.setJobFlag(newFlag);
                }
            }
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleSuccessEvent(SuccessEvent e) {
        JobKey jobKey = e.getJobKey();
        long taskId = e.getTaskId();
        DAGJob dagJob = waitingTable.get(jobKey);
        if (dagJob != null) {
            List<DAGJob> children = getChildren(dagJob);
            if (children != null) {
                for (DAGJob child : children) {
                    // 更新依赖状态为true
                    if (child.getJobFlag().equals(JobFlag.ENABLE)) {
                        child.setDependStatus(jobKey, taskId);
                        LOGGER.debug("Receive SuccessEvent, set depend status of {} to true.",
                                "jobKey="+child.getJobKey()+",preJobKey="+jobKey+",preTaskId="+taskId);
                        // 如果通过依赖检查，提交给taskScheduler，并重置自己的依赖状态
                        submitJobWithCheck(child);
                    }
                }
            }
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleFailedEvent(FailedEvent e) {
        JobKey jobKey = e.getJobKey();
        long taskId = e.getTaskId();
        DAGJob dagJob = waitingTable.get(jobKey);
        if (dagJob != null) {
            List<DAGJob> children = getChildren(dagJob);
            if (children != null) {
                for (DAGJob child : children) {
                    // 更新依赖状态为false
                    child.resetDependStatus(jobKey, taskId);
                    LOGGER.debug("Receive FailedEvent, reset depend status of {} to false.",
                            "jobKey="+child.getJobKey()+",preJobKey="+jobKey+",preTaskId="+taskId);
                }
            }
        }
    }

    /**
     * submit job if pass the dependency check
     *
     * @param dagJob
     */
    private void submitJobWithCheck(DAGJob dagJob) {
        List<DAGJob> parents = getParents(dagJob);
        Set<JobKey> needJobs = Sets.newHashSet();
        // get enabled parents
        if (parents != null) {
            for (DAGJob parent : parents) {
                if (parent.getJobFlag().equals(JobFlag.ENABLE)) {
                    needJobs.add(parent.getJobKey());
                }
            }
        }
        if (dagJob.dependCheck(needJobs)) {
            LOGGER.debug("DAGJob {} pass the depend check", dagJob.getJobKey());
            taskScheduler.submitJob(dagJob.getJobKey());
            // reset depend status
            dagJob.resetDependStatus();
        }
    }

    private List<DAGJob> getParents(DAGJob dagJob) {
        List<DAGJob> parents = new ArrayList<DAGJob>();
        Set<DefaultEdge> inEdges = dag.incomingEdgesOf(dagJob);
        if (inEdges != null) {
            for (DefaultEdge edge : inEdges) {
                parents.add(dag.getEdgeSource(edge));
            }
        }
        return parents;
    }

    private List<DAGJob> getChildren(DAGJob dagJob) {
        List<DAGJob> children = new ArrayList<DAGJob>();
        Set<DefaultEdge> outEdges = dag.outgoingEdgesOf(dagJob);
        if (outEdges != null) {
            for (DefaultEdge edge : outEdges) {
                children.add(dag.getEdgeTarget(edge));
            }
        }
        return children;
    }
}