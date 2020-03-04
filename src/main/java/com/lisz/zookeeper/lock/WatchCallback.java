package com.lisz.zookeeper.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class WatchCallback implements Watcher, AsyncCallback.StringCallback, AsyncCallback.Children2Callback, AsyncCallback.StatCallback {
    private ZooKeeper zk;
    private String threadName;
    private CountDownLatch latch = new CountDownLatch(1);
    private String pathName;

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public WatchCallback(ZooKeeper zk) {
        this.zk = zk;
    }

    public void tryLock() { // react 模型，返回值是void
        try {
            zk.create("/lock", threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, this, "ctx");
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void unLock() {
        try {
            zk.delete(pathName, -1);
            //System.out.println(threadName + " over work...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        latch = new CountDownLatch(1);
    }

    // Watcher
    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                zk.getChildren("/", false, this, "adf"); //只要是关于"/"的，就不需要watch
                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:
                break;
            case DataWatchRemoved:
                break;
            case ChildWatchRemoved:
                break;
        }
    }

    // StringCallback
    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        //System.out.println(path);
        if (name != null) {
            System.out.println(threadName + " creates node " + name);
            pathName = name;
            zk.getChildren("/", false, this, "adf");
            //latch.countDown();
        }

    }

    // Children2Callback
    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
        //进到这里说明StringCallback.processResult创建节点成功，看到的锁目录下的所有节点，且看到了自己和前面的所有节点
        System.out.println(threadName + " looks locks ... ");
        /*for (String child : children) {
            System.out.println(child);
        }*/
        Collections.sort(children);
        int index = children.indexOf(pathName.substring(1));
        if (index == 0) {
            System.out.println(threadName + " I'm the first...");
            try {
                zk.setData("/", threadName.getBytes(), -1); // 加了这么一步：谁获得锁了，就把线程信息写到锁目录里，让第一个线程别跑太快。但更关键的是重入锁，抢到了就做个标记。设置数这里不需要callback
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown(); //是当前队列的第一个，让 tryLock()继续往下走，并顺利退出，开始干活的业务
        } else {
            // 盯住index - 1，也就是前一个
            // 这里的这个watcher一定要写，不能是false，第二个this也一定要写，判断exits的一瞬间前面的节点挂（超时消失）了，有可能exists监控成功有可能不成功
            zk.exists("/" + children.get(index - 1), this, this, "adf");
        }
    }

    // StatCallback
    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        // stat != null 代表zk.exists方法发现节点已经存在了
    }
}
