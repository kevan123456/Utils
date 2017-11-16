package com.ws.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadDomain39 {
    private Lock lock = new ReentrantLock();

    public void methodA() {
        try {
            lock.lock();
            System.out.println("MethodA begin ThreadName = " + Thread.currentThread().getName());
            Thread.sleep(5000);
            System.out.println("MethodA end ThreadName = " + Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    public void methodB() {
        lock.lock();
        System.out.println("MethodB begin ThreadName = " + Thread.currentThread().getName());
        System.out.println("MethodB end ThreadName = " + Thread.currentThread().getName());
        lock.unlock();
    }
}
