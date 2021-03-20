package com.eric.distributed.lock.zk;

/**
 * zookeeper分布式锁接口
 *
 * @author EricShen
 * @date 2020-05-14
 */
public interface IZkLock {

    /**
     * 加锁
     */
    void zkLock();

    /**
     * 解锁
     */
    void zkUnlock();

}
