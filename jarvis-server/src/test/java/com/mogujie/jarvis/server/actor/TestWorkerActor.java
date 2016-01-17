package com.mogujie.jarvis.server.actor;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.google.inject.Injector;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.protocol.RegistryWorkerProtos;
import com.mogujie.jarvis.server.JarvisServer;
import com.mogujie.jarvis.server.actor.util.TestUtil;
import com.mogujie.jarvis.server.guice4test.Injectors4Test;
import com.mogujie.jarvis.server.service.WorkerService;
import com.typesafe.config.Config;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Location www.mogujie.com
 * Created by qinghuo on 16/1/14.
 * used by jarvis-parent
 */
public class TestWorkerActor {
    String authKey = "d03fa97612734db7bdee3bbb2cdbf993";
    Thread threadServer = null;

    @Before
    public void setup() {
        try {//检测server端口是否被占用
            if(TestUtil.isPortHasBeenUse("localhost", 10000) && TestUtil.isPortHasBeenUse(InetAddress.getLocalHost().getHostAddress(), 10000)){
                ServerProxy serverProxy = new ServerProxy();
                threadServer = new Thread(serverProxy);
                threadServer.start();
            }
        } catch (IOException e) {
            System.err.println("no port to use");
        }


    }

    @After
    public void tearDown() {
        if (threadServer != null) threadServer.stop();
        Injector injector = Injectors4Test.getInjector();
        WorkerService workerService = injector.getInstance(WorkerService.class);
        Assert.assertEquals(workerService.getWorkerId("127.0.0.1", 10003), 10);

    }

    @Test
    public void testWorkerRegister() {
        //测试绑定10003端口
        Config akkaConfig = ConfigUtils.getAkkaConfig("akka-test.conf");
        ActorSystem system = ActorSystem.create("worker", akkaConfig);
        ActorSelection serverActor = system.actorSelection("akka.tcp://server@127.0.0.1:10000/user/server");

        new JavaTestKit(system) {{
            RegistryWorkerProtos.WorkerRegistryRequest workerRegistryRequest = RegistryWorkerProtos.WorkerRegistryRequest.newBuilder().setKey(authKey).build();
            int flag = 0;
            while (flag < 10) {
                try {
                    serverActor.tell(workerRegistryRequest, getRef());

                    RegistryWorkerProtos.ServerRegistryResponse response
                            = (RegistryWorkerProtos.ServerRegistryResponse) receiveOne(duration("3 seconds"));
                    //  if(serverActor.path())
                    if (response.getSuccess()) {
                        Assert.assertEquals(response.getSuccess(), true);
                        break;
                    }
//                    response.ge
                    Assert.assertEquals(response.getSuccess(), true);

                } catch (NullPointerException ex) {
                    System.err.println("server not ready");
                }

                flag++;
                try {
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }};
    }

    class ServerProxy implements Runnable {

        @Override
        public void run() {
            String[] s = new String[0];
            try {
                new JarvisServer().main(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
