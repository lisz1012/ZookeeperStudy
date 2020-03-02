package com.lisz.zookeeper.config;

import com.lisz.zookeeper.WatchCallback;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;

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

    public void getConf() {
        myConf = new MyConf();
        WatchCallback watchCallback = new WatchCallback(zk);
        watchCallback.setMyConf(myConf);
        zk.exists("/AppConf", watchCallback, watchCallback, "ABC");
    }
}
