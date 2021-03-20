package com.eric;

import com.eric.order.OrderService;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {
        // getStandaloneNumber();
        //
        // getZkSimpleLockNumber();

        getEphSeqLock();

    }

    private static void getEphSeqLock() {
        for (int i = 0; i < 30; i++) {
            new Thread(() -> {
                new OrderService().getEphSeqClusterOrderNumber();
            }, String.valueOf(i)).start();
        }
    }

    private static void getZkSimpleLockNumber() {
        for (int i = 1; i <= 30; i++) {
            new Thread(() -> {
                new OrderService().getSimpleClusterOrderNumber();
            }, String.valueOf(i)).start();
        }
    }

    private static void getStandaloneNumber() {
        for (int i = 1; i <= 30; i++) {
            new Thread(() -> {
                try {
                    new OrderService().getStandaloneOrderNumber();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, String.valueOf(i)).start();
        }
    }
}
