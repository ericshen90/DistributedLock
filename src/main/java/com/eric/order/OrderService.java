package com.eric.order;

import com.eric.distributed.lock.zk.IZkLock;
import com.eric.distributed.lock.zk.optimization.ZkDistributedEphSeqLock;
import com.eric.distributed.lock.zk.simple.ZkDistributedSimpleLock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author EricShen
 * @date 2020-05-14
 */
public class OrderService {

    private GetOrderNumberUtil getOrderNumberUtil = new GetOrderNumberUtil();
    private Lock standaloneLock = new ReentrantLock();
    private IZkLock zkDistributedSimpleLock = new ZkDistributedSimpleLock();

    private ZkDistributedEphSeqLock zkDistributedEphSeqLock = new ZkDistributedEphSeqLock();

    public void getStandaloneOrderNumber() {
        standaloneLock.lock();
        try {
            System.out.println(
                Thread.currentThread().getName() + "\t---->" + getOrderNumberUtil.getNumber());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            standaloneLock.unlock();
        }
    }

    public void getSimpleClusterOrderNumber() {
        zkDistributedSimpleLock.zkLock();
        try {
            System.out.println(
                "线程" + Thread.currentThread().getName() + "\t---->" + getOrderNumberUtil
                    .getNumber());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            zkDistributedSimpleLock.zkUnlock();
        }
    }

    public void getEphSeqClusterOrderNumber() {
        zkDistributedEphSeqLock.zkLock();
        try {
            System.out.println(
                "线程" + Thread.currentThread().getName() + "\t---->" + getOrderNumberUtil
                    .getNumber());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            zkDistributedEphSeqLock.zkUnlock();
        }
    }


}
