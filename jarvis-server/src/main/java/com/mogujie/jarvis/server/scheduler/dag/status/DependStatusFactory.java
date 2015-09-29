/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月21日 下午2:39:43
 */

package com.mogujie.jarvis.server.scheduler.dag.status;

import org.apache.commons.configuration.Configuration;

import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.core.util.ReflectionUtils;
import com.mogujie.jarvis.dto.JobDepend;
import com.mogujie.jarvis.server.scheduler.dag.strategy.AbstractOffsetStrategy;
import com.mogujie.jarvis.server.scheduler.dag.strategy.CommonStrategy;
import com.mogujie.jarvis.server.scheduler.dag.strategy.OffsetStrategyFactory;
import com.mogujie.jarvis.server.service.JobDependService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class DependStatusFactory {
    public static String JOB_DEPEND_STATUS_KEY = "job.depend.status";
    public static String DEFAULT_JOB_DEPEND_STATUS = MysqlCachedDependStatus.class.getName();

    public static AbstractDependStatus create(long myJobId, long preJobId) throws ClassNotFoundException {
        AbstractDependStatus dependStatus = null;
        JobDependService jobDependService = SpringContext.getBean(JobDependService.class);
        if (jobDependService != null) {
            JobDepend jobDepend = jobDependService.getRecord(myJobId, preJobId);
            if (jobDepend != null) {
                CommonStrategy commonStrategy = CommonStrategy.getInstance(jobDepend.getCommonStrategy());
                Pair<AbstractOffsetStrategy, Integer> offsetStrategyPair = OffsetStrategyFactory.create(jobDepend.getOffsetStrategy());
                if (offsetStrategyPair != null) {
                    dependStatus = new OffsetDependStatus(myJobId, preJobId, commonStrategy,
                            offsetStrategyPair.getFirst(), offsetStrategyPair.getSecond());
                } else {
                    Configuration conf = ConfigUtils.getServerConfig();
                    String className = conf.getString(JOB_DEPEND_STATUS_KEY, DEFAULT_JOB_DEPEND_STATUS);
                    dependStatus = ReflectionUtils.getInstanceByClassName(className);
                    dependStatus.setMyjobId(myJobId);
                    dependStatus.setPreJobId(preJobId);
                    dependStatus.setCommonStrategy(commonStrategy);
                }
            }
        }

        return dependStatus;
    }
}
