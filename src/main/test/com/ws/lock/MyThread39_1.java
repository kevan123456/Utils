package com.ws.lock;

public class MyThread39_1 extends Thread{
    private ThreadDomain39 td;

    public MyThread39_1(ThreadDomain39 td) {
        this.td = td;
    }

    @Override
    public void run() {
        td.methodB();
    }

}
