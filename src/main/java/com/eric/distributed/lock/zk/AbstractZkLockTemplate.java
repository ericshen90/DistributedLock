package com.eric.distributed.lock.zk;

import org.I0Itec.zkclient.ZkClient;

/**
 * zookeeper分布式锁抽象模板类
 *
 * @author EricShen
 * @date 2020-05-15
 */
public abstract class AbstractZkLockTemplate implements IZkLock {

    /**
     * zk服务器ip:port
     */
    protected static final String ZK_SERVER = "127.0.0.1:2181";

    /**
     * zk连接超时时间
     */
    protected static final int TIME_OUT = 45 * 1000;

    /**
     * zk客户端
     */
    protected static ZkClient zkClient = new ZkClient(ZK_SERVER, TIME_OUT);

    /**
     * 节点
     */
    protected static final String NODE = "/zkNodeLock";

    /**
     * 删除节点
     */
    protected volatile String path = NODE;

    /**
     * 尝试加锁
     *
     * @return
     */
    protected abstract boolean tryZkLock();

    /**
     * 等待加锁
     */
    protected abstract void waitZkLock();

    /**
     * 加锁
     */
    @Override
    public void zkLock() {
        if (tryZkLock()) {
            System.out.println(Thread.currentThread().getName() + "\t占用zk锁成功");
        } else {
            waitZkLock();
            zkLock();
        }

    }

    /**
     * 解锁
     */
    @Override
    public void zkUnlock() {
        if (zkClient != null) {
            // 删除节点
            zkClient.delete(path);
        }
        System.out.println(Thread.currentThread().getName() + "\t释放zk锁成功");
        System.out.println();
    }

}
