package com.kkb.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;

public class CuratorClient {
    private static final String ZK_ADDRESS = "node:2181,node01:2181,node03:2181";
    private static final String ZK_PATH = "/zk_test";
    static CuratorFramework client = null;

    public static void main(String[] args) throws Exception {
        init();
//        createPersistentZNode();
//        createEphemeralZNode();
//        queryZNodeData();
//        modifyZNodeData();
//        deleteZNode();
        watchZNode();
        clean();
    }

    public static void init() {
        RetryNTimes retryPolicy = new RetryNTimes(10, 5000);
        client = CuratorFrameworkFactory.newClient(ZK_ADDRESS, retryPolicy);
        client.start();
        System.out.println("zk client start successfully!");
    }

    public static void createPersistentZNode() throws Exception {
        String zNodeData = "火辣的";

        client.create().
                creatingParentsIfNeeded().
                withMode(CreateMode.PERSISTENT).
                forPath("/beijing/goddess/angelababy", zNodeData.getBytes());
    }

    public static void createEphemeralZNode() throws Exception {
        String zNodeData2 = "流星雨";

        client.create().
                creatingParentsIfNeeded().
                withMode(CreateMode.EPHEMERAL).
                forPath("/beijing/star", zNodeData2.getBytes());

        Thread.sleep(10000);
    }

    public static void queryZNodeData() throws Exception {
        print("ls", "/");
        print(client.getChildren().forPath("/"));

        print("get", ZK_PATH);
        if (client.checkExists().forPath(ZK_PATH) != null) {
            print(client.getData().forPath(ZK_PATH));
        } else {
            System.out.println("节点不存在");
        }
    }

    public static void modifyZNodeData() throws Exception {
        String data2 = "world";
        print("set", ZK_PATH, data2);

        client.setData().forPath(ZK_PATH, data2.getBytes());
        print("get", ZK_PATH);
        print(client.getData().forPath(ZK_PATH));
    }

    public static void deleteZNode() throws Exception {
        print("delete", ZK_PATH);

        client.delete().forPath(ZK_PATH);
        print(client.getChildren().forPath("/"));
    }

    public static void watchZNode() throws Exception {
        TreeCache treeCache = new TreeCache(client, ZK_PATH);
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                ChildData data = event.getData();
                if (data != null) {
                    switch (event.getType()) {
                        case NODE_ADDED:
                            System.out.println("NODE_ADDED : " + data.getPath() + "  数据:" + new String(data.getData()));
                            break;
                        case NODE_REMOVED:
                            System.out.println("NODE_REMOVED : " + data.getPath() + "  数据:" + new String(data.getData()));
                            break;
                        case NODE_UPDATED:
                            System.out.println("NODE_UPDATED : " + data.getPath() + "  数据:" + new String(data.getData()));
                            break;
                        default:
                            break;
                    }
                } else {
                    System.out.println("data is null : " + event.getType());
                }
            }
        });

        treeCache.start();
        Thread.sleep(30000);
        System.out.println("关闭treeCache");
        treeCache.close();
    }

    public static void clean() {
        System.out.println("close session");
        client.close();
    }

    private static void print (String... cmds) {
        StringBuilder text = new StringBuilder("$ ");
        for (String cmd : cmds) {
            text.append(cmd).append(" ");
        }
        System.out.println(text.toString());
    }

    private static void print(Object result) {
        System.out.println(
            result instanceof byte[] ? new String((byte[]) result) : result
        );
    }
}
