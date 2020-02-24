package com.lisz.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) throws Exception {
        System.out.println( "Hello World!" );
        final CountDownLatch latch = new CountDownLatch(1);
        //zk是有session概念的，没有连接池的概念
        //watch有两类，第一类是在newzk的时候传入的watch。这个watch是session 级别的跟path、node是没有关系的，zk中所有的节点的变化是收不到事件的，只能收到断掉连接重新连别人的过程
        //watch的注册只发生在读类型调用时，比如get、exists。因为写方法是产生事件的
        final ZooKeeper zk = new ZooKeeper("192.168.1.131:2181,192.168.1.132:2181,192.168.1.133:2181,192.168.1.134:2181", 10000,
                new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        Event.KeeperState state = event.getState();
                        Event.EventType type = event.getType();
                        String path = event.getPath();
                        System.out.println("New ZK watcher: " + event.toString());

                        switch (state) {
                            case Unknown:
                                break;
                            case Disconnected:
                                break;
                            case NoSyncConnected:
                                break;
                            case SyncConnected:
                                System.out.println("Connected...");
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

                        switch (type) {
                            case None:
                                break;
                            case NodeCreated:
                                break;
                            case NodeDeleted:
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
                });

        latch.await();
        ZooKeeper.States state = zk.getState();
        switch (state) {
            case CONNECTING:
                System.out.println("Connecting...");
                break;
            case ASSOCIATING:
                break;
            case CONNECTED:
                System.out.println("Connected.");
                break;
            case CONNECTEDREADONLY:
                break;
            case CLOSED:
                break;
            case AUTH_FAILED:
                break;
            case NOT_CONNECTED:
                break;
        }

        String pathName = zk.create("/ooxx", "olddata".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);//带序列的节点必须拿到返回值才知道叫啥

        final Stat stat = new Stat();
        byte[] data = zk.getData("/ooxx", new Watcher() {
            @Override
            public void process(final WatchedEvent event) { //上面的watcher只跟session有关系，跟path没关系，这里的watcher跟path有关系，而且这个path上的watcher是一次性的
                System.out.println("getData watcher: " + event.toString()); //getData拿到数据的时候不会调用，什么时候有事件的时候什么时候回调
                //zk.register(this); 这样不对，只会改变defaultwatcher
                try {
                    //true的时候是default watcher被重新注册，new Zookeeper的时候传入的watcher, 还用当前这个在path上的watcher的话就把true换成this，然后getData watcher:就可以打印两遍了
                    zk.getData("/ooxx", this, stat);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, stat);
        System.out.println(new String(data));
        // zkCli.sh查看 /ooxx节点，它只会存在10秒，因为设置的timeout是10秒

        Stat stat1 = zk.setData("/ooxx", "newdata".getBytes(), 0); //数据被修改，上面的getData watch回调要发生了
        Stat stat2 = zk.setData("/ooxx", "newdata_2".getBytes(), stat1.getVersion()); //数据被修改，但watcher不会被出发，watcher只是一次性的

        System.out.println("------ async starts ------");
        zk.getData("/ooxx", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) { //状态码、路径、getData最后的那个ctx = "abc"参数、数据、元数据
                System.out.println("------ async callback ------"); //先打印下面的------ async over ------，然后再打印这里的内容。回调方法的好处是：你是方法内容的缔造者，而不是逻辑执行顺序的缔造者，后者由框架决定，减少阻塞和空转
                System.out.println(ctx.toString());
                System.out.println(new String(data));
            }
        }, "abc");
        System.out.println("------ async over ------");

        Thread.sleep(2000000);
    }
}
