package com.lisz.zookeeper2;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class WatcherCallback implements Watcher, AsyncCallback.StatCallback, AsyncCallback.DataCallback {
    private ZooKeeper zk;
    private CountDownLatch latch = new CountDownLatch(1);
    private MyConf conf;

    public WatcherCallback(ZooKeeper zk, MyConf conf) {
        this.zk = zk;
        this.conf = conf;
    }

    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        if (data != null) {
            String s = new String(data);
            conf.setConf(s);
            latch.countDown();
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        if (stat != null) {
            zk.getData("/App2", this, this, "ABC");
        }
    }

    @Override
    public void process(WatchedEvent event) {
        Event.EventType type = event.getType();
        switch (type) {
            case None:
                break;
            case NodeCreated:
                zk.getData("/App2", this, this, "ABC");
                break;
            case NodeDeleted:
                conf.setConf("");
                latch = new CountDownLatch(1);
                break;
            case NodeDataChanged:
                zk.getData("/App2", this, this, "ABC");
                break;
            case NodeChildrenChanged:
                break;
            case DataWatchRemoved:
                break;
            case ChildWatchRemoved:
                break;
        }
    }

    public void await() {
        zk.exists("/App2", this, this, "ABC");
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
