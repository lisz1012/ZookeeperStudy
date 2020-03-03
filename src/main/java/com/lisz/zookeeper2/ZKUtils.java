package com.lisz.zookeeper2;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZKUtils {
    private static String address = "192.168.1.131:2181,192.168.1.132:2181,192.168.1.133:2181,192.168.1.134:2181/testConf2";
    private static CountDownLatch latch = new CountDownLatch(1);
    private static DefaultWatcher watcher = new DefaultWatcher();

    public static ZooKeeper getZk() {
        ZooKeeper zk = null;
        try {
            watcher.setLatch(latch);
            zk = new ZooKeeper(address, 1000, watcher);
            latch.await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return zk;
    }
}
