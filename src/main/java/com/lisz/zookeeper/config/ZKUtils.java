package com.lisz.zookeeper.config;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZKUtils {
    private static ZooKeeper zk;
    // 下面可以指定最高级别的path节点，本程序中的/，最高就能看到testConf
    private static String address = "192.168.1.131:2181,192.168.1.132:2181,192.168.1.133:2181,192.168.1.134:2181/testConf";
    private static DefaultWatcher watcher = new DefaultWatcher();
    private static CountDownLatch latch = new CountDownLatch(1);

    public static ZooKeeper getZK() {
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
