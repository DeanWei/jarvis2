/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2016年2月2日 下午3:34:47
 */

package com.mogujie.jarvis.server.service;


import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mogujie.jarvis.dao.generate.PlanMapper;
import com.mogujie.jarvis.dto.generate.Plan;
import com.mogujie.jarvis.dto.generate.PlanExample;
import com.mogujie.jarvis.server.util.PlanUtil;

/**
 * @author guangming
 *
 */
@Singleton
public class PlanService {

    @Inject
    private PlanMapper planMapper;

    @Inject
    private JobService jobService;

    public void updateJobIds(Set<Long> jobIds) {
        PlanExample example = new PlanExample();
        example.createCriteria();
        planMapper.deleteByExample(example);

        for (long jobId : jobIds) {
            Plan plan = new Plan();
            plan.setJobId(jobId);
            plan.setCreateTime(DateTime.now().toDate());
            if (planMapper.selectByPrimaryKey(jobId) == null) {
                planMapper.insertSelective(plan);
            }
        }
    }

    public void updateJobIds(Range<DateTime> range) {
        List<Long> activeJobIds = jobService.getEnableActiveJobIds();
        Set<Long> planJobIds = Sets.newHashSet();
        for (long jobId : activeJobIds) {
            DateTime nextTime = PlanUtil.getScheduleTimeAfter(jobId, range.lowerEndpoint().minusSeconds(1));
            if (nextTime != null && range.contains(nextTime)) {
                planJobIds.add(jobId);
            }
        }
        updateJobIds(planJobIds);
    }
}
