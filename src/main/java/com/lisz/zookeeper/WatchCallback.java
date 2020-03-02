package com.lisz.zookeeper;

import com.lisz.zookeeper.config.MyConf;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class WatchCallback implements Watcher, AsyncCallback.StatCallback, AsyncCallback.DataCallback {
    private ZooKeeper zk;
    private MyConf myConf;

    public WatchCallback(ZooKeeper zk) {
        this.zk = zk;
    }

    public MyConf getMyConf() {
        return myConf;
    }

    public void setMyConf(MyConf myConf) {
        this.myConf = myConf;
    }

    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {

    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        if (stat != null) { // stat != null 代表节点已经存在了
            zk.getData("/AppConf", this, this, "sdfs");
        }
    }

    @Override
    public void process(WatchedEvent event) {

    }
}
