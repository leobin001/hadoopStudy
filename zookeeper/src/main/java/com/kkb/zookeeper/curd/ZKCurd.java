package com.kkb.zookeeper.curd;


import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ZKCurd {
    private static final int SESSION_TIME = 30000;
    private static final String ZK_SERVERS = "node:2181,node01:2181,node03:2181";
    private static final Logger LOGGER = Logger.getLogger(ZKCurd.class);

    static ZooKeeper zk = null;

    Watcher watcher = new Watcher() {
        public void process(WatchedEvent watchedEvent) {
            LOGGER.info("event:" + watchedEvent.toString());
        }
    };

    @Before
    public void connect() throws Exception {
        zk = new ZooKeeper(ZK_SERVERS, SESSION_TIME, this.watcher);
        long sessionId = zk.getSessionId();
        LOGGER.info("sessionId:" + sessionId);
    }

    @After
    public void closeZk() throws Exception {
        zk.close();
    }

    @Test
    public void createPersistentNode() throws Exception {
        String result = zk.create("/kkb", "kkbdata1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        LOGGER.info("create result: " + result);
        LOGGER.info("查看zk_test节点是否存在:" + zk.getData("/zk_test", false, null));
    }

    @Test
    public void testDeleteZnode() throws Exception {
        zk.delete("/kkb", -1);
        if (null == zk.exists("/kkb", false)) {
            System.out.println("节点删除成功!");
        }
    }

    @Test
    public void createEphemeralNode() {
        try {
            zk.create("/tmp", "tmpdata".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            Thread.sleep(20000);
            LOGGER.info("/tmp是否存在：" + zk.exists("/tmp", null));
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        } catch (KeeperException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void exist() throws Exception {
        Stat stat = zk.exists("/kkb", null);
        if (stat != null) {
            System.out.println(stat.getVersion());
            LOGGER.info("node exists!");
        } else {
            LOGGER.info("node not exists!");
        }
    }

    @Test
    public void testGetData() throws KeeperException, InterruptedException {
        byte[] data = zk.getData("/kkb", null, null);
        System.out.println("data is :" + new String(data));
    }

    @Test
    public void testSetData(String path, byte[] data) throws KeeperException, InterruptedException {
        Stat stat = zk.setData(path, data, -1);
        if (null != stat) {
            System.out.println("内容设置成功!");
        }
    }

    @Test
    public void testGetDataWatch() throws KeeperException, InterruptedException {
        byte[] data = zk.getData("/kkb", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                LOGGER.info("===>>> even type: " + event.getType());
                System.out.println("triger watcher!");
            }
        }, null);
        System.out.println("------------->>> data is " + new String(data));

        System.out.println("the first time to set data");
        zk.setData("/kkb", "kkbdata2".getBytes(), -1);

        System.out.println("the second time to set data");
        zk.setData("/kkb", "kkbdata2".getBytes(), -1);
    }
}
