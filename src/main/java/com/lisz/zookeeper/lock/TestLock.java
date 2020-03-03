package com.lisz.zookeeper.lock;

import com.lisz.zookeeper.config.ZKUtils;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TestLock {
    private ZooKeeper zk;

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
    public void lock() {
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                WatchCallback watchCallback = new WatchCallback(zk);
                String threadName = Thread.currentThread().getName();
                watchCallback.setThreadName(threadName);
                //每个线程去抢锁
                watchCallback.tryLock();
                //干活
                System.out.println(threadName + " working..");
                /*try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                //释放锁
                watchCallback.unLock();
            }).start();
        }

        while (true) {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /*
Thread-5 looks locks ...     5号线程太快了，其他的线程还没来得及监听上删除事件就发生过了
Thread-5 I'm the first...
Thread-9 looks locks ...
Thread-5 working..
Thread-4 looks locks ...
Thread-8 looks locks ...
Thread-1 looks locks ...
Thread-0 looks locks ...
         */
    }
}
