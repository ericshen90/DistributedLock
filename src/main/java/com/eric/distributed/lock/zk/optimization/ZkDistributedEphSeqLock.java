package com.eric.distributed.lock.zk.optimization;

import com.eric.distributed.lock.zk.AbstractZkLockTemplate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.commons.lang3.StringUtils;

/**
 * zookeeper分布式锁优化实现
 *
 * Ephemeral->解决死锁
 * Sequential->解决羊群效应
 *
 * @author EricShen
 * @date 2020-05-14
 */
public class ZkDistributedEphSeqLock extends AbstractZkLockTemplate {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    /**
     * 排序中上一个节点
     */
    private volatile String beforePath;

    /**
     * 子节点名
     */
    private static final String LOCK_DATA = "lock";

    public ZkDistributedEphSeqLock() {
        this.path = null;
        if (!zkClient.exists(NODE)) {
            try {
                // 创建根节点
                zkClient.createPersistent(NODE);
            } catch (ZkNodeExistsException e) {
                System.out.println("忽略已创建NODE节点");
            }
        }
    }

    @Override
    public synchronized boolean tryZkLock() {
        // 初始化创建临时有序子节点
        if (StringUtils.isBlank(path)) {
            path = zkClient.createEphemeralSequential(NODE + "/", LOCK_DATA);
        }

        // 获取父节点所有子节点
        List<String> children = zkClient.getChildren(NODE);
        // 从小到大排序
        Collections.sort(children);

        // 当前节点是最小节点，则加锁成功
        if (path.equals(NODE + "/" + children.get(0))) {
            return true;
        }

        // 非最小节点，则获取上一个节点
        int i = Collections.binarySearch(children, path.substring(NODE.length() + 1));
        beforePath = NODE + "/" + children.get(i - 1);

        return false;
    }


    @Override
    public void waitZkLock() {
        // zk数据监听接口
        IZkDataListener iZkDataListener = new IZkDataListener() {
            /**
             * 修改监听
             * @param dataPath
             * @param data
             * @throws Exception
             */
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
            }

            /**
             * 删除监听
             * @param dataPath
             * @throws Exception
             */
            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                countDownLatch.countDown();
            }
        };
        // 订阅节点
        zkClient.subscribeDataChanges(beforePath, iZkDataListener);
        if (zkClient.exists(beforePath)) {
            try {
                // 线程等待zk订阅节点删除通知
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 取消订阅
        zkClient.unsubscribeDataChanges(beforePath, iZkDataListener);
    }


}
