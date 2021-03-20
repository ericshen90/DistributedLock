package com.eric.distributed.lock.zk.simple;

import com.eric.distributed.lock.zk.AbstractZkLockTemplate;
import java.util.concurrent.CountDownLatch;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

/**
 * zookeeper分布式锁简单实现
 *
 * 存在死锁及羊群效应
 *
 * @author EricShen
 * @date 2020-05-14
 */
public class ZkDistributedSimpleLock extends AbstractZkLockTemplate {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    @Override
    protected boolean tryZkLock() {
        try {
            // 创建临时节点，客户端quit后自动清除
            zkClient.createPersistent(NODE);
            return true;
        } catch (ZkNodeExistsException e) {
            // 若节点已存在，占用锁失败
            return false;
        }
    }


    @Override
    protected void waitZkLock() {
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
        zkClient.subscribeDataChanges(NODE, iZkDataListener);
        if (zkClient.exists(NODE)) {
            try {
                // 线程等待zk订阅节点删除通知
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 取消订阅
        zkClient.unsubscribeDataChanges(NODE, iZkDataListener);
    }
}
