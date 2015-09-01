/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月17日 上午10:30:12
 */
package com.mogujie.jarvis.core.common.util;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import com.google.common.base.Throwables;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * @author wuya
 *
 */
public class ConfigUtils {

    private static PropertiesConfiguration workerConfig;
    private static PropertiesConfiguration serverConfig;

    public synchronized static Configuration getWorkerConfig() {
        if (workerConfig == null) {
            try {
                workerConfig = new PropertiesConfiguration("worker.properties");
                workerConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
            } catch (ConfigurationException e) {
                Throwables.propagate(e);
            }
        }

        return workerConfig;
    }

    public synchronized static Configuration getServerConfig() {
        if (serverConfig == null) {
            try {
                serverConfig = new PropertiesConfiguration("server.properties");
                serverConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
            } catch (ConfigurationException e) {
                Throwables.propagate(e);
            }
        }

        return serverConfig;
    }

    public static Config getAkkaConfig() {
        try {
            String ipv4 = Inet4Address.getLocalHost().getHostAddress();
            return ConfigFactory.parseString("akka.remote.netty.tcp.hostname=" + ipv4);
        } catch (UnknownHostException e) {
            Throwables.propagate(e);
        }

        return null;
    }

}
