package com.lisz.zookeeper2;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

public class DefaultWatcher implements Watcher {
    private CountDownLatch latch;

    @Override
    public void process(WatchedEvent event) {
        Event.KeeperState state = event.getState();
        switch (state) {
            case Unknown:
                break;
            case Disconnected:
                break;
            case NoSyncConnected:
                break;
            case SyncConnected:
                latch.countDown();
                break;
            case AuthFailed:
                break;
            case ConnectedReadOnly:
                break;
            case SaslAuthenticated:
                break;
            case Expired:
                break;
            case Closed:
                break;
        }
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }
}
