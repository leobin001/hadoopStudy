package com.kkb.zookeeper.distributedLock;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DistributedLock implements Watcher {
    private static final int SESSION_TIME = 30000;
    private int threadId;
    private ZooKeeper zk = null;
    private String selfPath;
    private String waitPath;
    private String LOG_PREFIX_OF_THREAD;
    private static final int SESSION_TIMEOUT = 10000;
    private static final String GROUP_PATH = "/disLocks";
    private static final String SUB_PATH = "/disLocks/sub";
    private static final String CONNECTION_STRING = "node01:2181,node02:2181,node03:2181";
    private static final int THREAD_NUM = 10;

    private CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static final CountDownLatch threadSemaphore = new CountDownLatch(THREAD_NUM);
    private static final Logger LOG = Logger.getLogger(DistributedLock.class);

    public DistributedLock(int id) {
        this.threadId = id;
        LOG_PREFIX_OF_THREAD = "【第" + threadId + "个线程】";
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        for (int i = 0; i < THREAD_NUM; i++) {
            final int threadId = i + 1;
            new Thread() {
                @Override
                public void run() {
                    try {
                        DistributedLock dc = new DistributedLock(threadId);
                        dc.createConnection(CONNECTION_STRING, SESSION_TIMEOUT);
                        System.out.println("3、在createConnection中，线程等待结束，向下执行");

                        synchronized (threadSemaphore) {
                            dc.createPath(GROUP_PATH, "该节点由线程" + threadId + "创建", true);
                        }

                        dc.getLock();
                    } catch (Exception e) {
                        LOG.error("【第" + threadId + "个线程】 抛出的异常：");
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        try {
            threadSemaphore.await();
            LOG.info("所有线程运行结束!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getLock() throws KeeperException, InterruptedException {
        selfPath = zk.create(SUB_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        LOG.info(LOG_PREFIX_OF_THREAD + "创建锁路径:" + selfPath);

        if (checkMinPath()) {
            getLockSuccess();
        }
    }

    private void getLockSuccess() throws KeeperException, InterruptedException {
        if (zk.exists(this.selfPath, false) == null) {
            LOG.error(LOG_PREFIX_OF_THREAD + "本节点已不在了...");
            return;
        }
        LOG.info(LOG_PREFIX_OF_THREAD + "获取锁成功，赶紧干活！");
        Thread.sleep(2000);
        LOG.info(LOG_PREFIX_OF_THREAD + "删除本节点：" + selfPath);
        zk.delete(this.selfPath, -1);
        releaseConnection();
        threadSemaphore.countDown();
    }

    private void releaseConnection() {
        if (this.zk != null) {
            try {
                this.zk.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LOG.info(LOG_PREFIX_OF_THREAD + "释放连接");
    }

    private boolean checkMinPath() throws KeeperException, InterruptedException {
        List<String> subNodes = zk.getChildren(GROUP_PATH, false);
        Collections.sort(subNodes);

        int index = subNodes.indexOf(selfPath.substring(GROUP_PATH.length() + 1));
        switch (index) {
            case -1:
                LOG.error(LOG_PREFIX_OF_THREAD + "本节点已不在了..." + selfPath);
                return false;
            case 0:
                LOG.info(LOG_PREFIX_OF_THREAD + "子节点中，我果然是老大" + selfPath);
                //获得锁
                return true;
            default:
                this.waitPath = GROUP_PATH + "/" + subNodes.get(index - 1);
                LOG.info(LOG_PREFIX_OF_THREAD + "获取子节点中，排在我前面的" + waitPath);
                try {
                    zk.getData(waitPath, true, new Stat());
                    return false;
                } catch (KeeperException e) {
                    if (zk.exists(waitPath, false) == null) {
                        LOG.info(LOG_PREFIX_OF_THREAD + "子节点中，排在我前面的" + waitPath + "已失踪，幸福来得太突然?");
                        return checkMinPath();
                    } else {
                        throw e;
                    }
                }
        }
    }

    private void createPath(String path, String data, boolean needWatch) throws KeeperException, InterruptedException {
        if (zk.exists(path, needWatch) == null) {
            LOG.warn(LOG_PREFIX_OF_THREAD + "节点创建成功, Path: "
                    + this.zk.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)
                    + ", content: " + data);
        }
    }

    private void createConnection(String connectionString, int sessionTimeout) throws IOException, InterruptedException {
        zk = new ZooKeeper(connectionString, sessionTimeout, this);
        System.out.println("1、创建连接，并等待");
        connectedSemaphore.await();
        System.out.println("2、创建连接后，等待结束；理应执行");
    }


    @Override
    public void process(WatchedEvent event) {
        if (event == null) {
            return;
        }
        Event.KeeperState keeperState = event.getState();
        Event.EventType eventType = event.getType();
        if (Event.KeeperState.SyncConnected == keeperState) {
            if (Event.EventType.None == eventType) {
                LOG.info(LOG_PREFIX_OF_THREAD + "成功连接上ZK服务器");
                System.out.println("4、createConnection后，客户端成功连接zkServer");
                connectedSemaphore.countDown();
                System.out.println("5、CountDownLatch: connectedSemaphore 递减为0；理应执行 -> 2、创建连接后，等待结束");
            } else if (event.getType() == Event.EventType.NodeDeleted && event.getPath().equals(waitPath)) {
                LOG.info(LOG_PREFIX_OF_THREAD + "收到情报，排我前面的家伙已挂，我是不是可以出山了？");
                try {
                    if (checkMinPath()) {
                        getLockSuccess();
                    }
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (Event.KeeperState.Disconnected == keeperState) {
            LOG.info(LOG_PREFIX_OF_THREAD + "与ZK服务器断开连接");
        } else if (Event.KeeperState.AuthFailed == keeperState) {
            LOG.info(LOG_PREFIX_OF_THREAD + "权限检查失败");
        } else if (Event.KeeperState.Expired == keeperState) {
            LOG.info(LOG_PREFIX_OF_THREAD + "会话失效");
        }
    }
}
