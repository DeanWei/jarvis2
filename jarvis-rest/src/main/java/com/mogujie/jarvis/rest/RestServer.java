/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月14日 上午10:32:59
 */

package com.mogujie.jarvis.rest;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import com.mogujie.jarvis.rest.controller.JobController;
import com.mogujie.jarvis.rest.controller.LogController;
import com.mogujie.jarvis.rest.controller.SystemController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;

/**
 * 启动RestServer
 *
 */
public class RestServer {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) throws IOException {
        LOGGER.info("Starting rest server...");

        int port = ConfigUtils.getServerConfig().getInt("rest.http.port", 8080);

        //控制前注册
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(new SystemController());
        resourceConfig.register(new JobController());
        resourceConfig.register(new LogController());


        URI baseUri = UriBuilder.fromUri("http://" + Inet4Address.getLocalHost().getHostAddress() + "/").port(port).build();
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, resourceConfig);
        server.start();

        LOGGER.info("Rest server started.");
    }

}
