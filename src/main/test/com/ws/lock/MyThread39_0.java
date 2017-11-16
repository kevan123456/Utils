package com.ws.lock;

public class MyThread39_0 extends Thread{

    private ThreadDomain39 td;

    public MyThread39_0(ThreadDomain39 td){
        this.td = td;
    }

    @Override
    public void run(){
        td.methodA();
    }
}
