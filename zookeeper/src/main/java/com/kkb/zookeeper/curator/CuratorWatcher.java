package com.kkb.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.ZKPaths;

public class CuratorWatcher {
    private static final String ZK_ADDRESS = "note01:2181,node02:2181,node03:2181";
    private static final String ZK_PATH = "/zk_test";

    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient(ZK_ADDRESS, new RetryNTimes(10, 5000));
        client.start();
        System.out.println("zk client start successfully!");

        PathChildrenCache pathCache = new PathChildrenCache(client, ZK_PATH, true);

        PathChildrenCacheListener listener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                switch (event.getType()) {
                    case CHILD_ADDED:
                        System.out.println("Node added: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                        break;
                    case CHILD_UPDATED:
                        System.out.println("Node changed: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                        break;
                    case CHILD_REMOVED:
                        System.out.println("Node removed: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                        break;
                    default:
                        break;
                }
            }
        };

        pathCache.getListenable().addListener(listener);

        pathCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        System.out.println("Register zk pathCache successfully!");

        Thread.sleep(60000);
        pathCache.close();

        client.close();
    }
}
