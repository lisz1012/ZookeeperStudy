package com.lisz.zookeeper.config;

import com.lisz.zookeeper.WatchCallback;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TestConfig {
    private ZooKeeper zk;
    private MyConf myConf;

    @Before
    public void connect() {
        zk = ZKUtils.getZK();
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
    public void getConf() {
        myConf = new MyConf();
        WatchCallback watchCallback = new WatchCallback(zk);
        watchCallback.setConf(myConf);

        // 无论节点存在不存在，都去取数据，取到了再往下走
        watchCallback.await();
        // 1。 节点不存在
        // 2。 节点存在
        // 并没有把每种场景都线性写代码把堆积起来，而是通过callback和watch把各个部分代码粘连起来

        while (true) {
            if (myConf.getConf().equals("")) {
                System.out.println("conf 丢了 。。。");
                watchCallback.await();
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
