package com.lisz.zookeeper2;

import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestConf {
    private ZooKeeper zk;
    private MyConf myConf;

    @Before
    public void init() {
        zk = ZKUtils.getZk();
    }

    @After
    public void close() {
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() {
        myConf = new MyConf();
        WatcherCallback watcherCallback = new WatcherCallback(zk, myConf);
        watcherCallback.await();

        while (true) {
            if (myConf.getConf().equals("")) {
                System.out.println("conf 丢了 ...");
                watcherCallback.await();
            } else {
                System.out.println(myConf.getConf());
            }

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
