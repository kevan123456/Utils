package com.ws.lock;

public class DeadLockTest{

    final static Object obj_1 = new Object();
    final static Object obj_2 = new Object();

    public static void main(String[] args){
        Thread t1 = new Thread("t1"){
            public void run(){
                System.out.println("thread t1 start...");
                synchronized(obj_1){
                    try{
                        System.out.println("thread t1 sleep...");
                        Thread.sleep(100);
                    }catch(InterruptedException e){

                    }
                    System.out.println("lock obj_1");
                    synchronized(obj_2){
                        System.out.println("thread t1 done.");
                    }
                }


            }
        };

        Thread t2 = new Thread("t2"){
            public void run(){
                System.out.println("thread t2 start...");
                synchronized(obj_2){
                    System.out.println("lock obj_2");
                    synchronized(obj_1){
                        System.out.println("thread t2 done.");
                    }

                }

            }
        };
        t1.start();
        t2.start();
    }

}
