/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月8日 上午10:26:00
 */

package com.mogujie.jarvis.server.scheduler;

import com.google.common.eventbus.Subscribe;
import com.mogujie.jarvis.server.observer.Observer;

/**
 * @author guangming
 *
 */
public interface Scheduler extends Observer {

    @Subscribe
    public void handleInitEvent(InitEvent event);

    @Subscribe
    public void handleStartEvent(StartEvent event);

    @Subscribe
    public void handleStopEvent(StopEvent event);
}
