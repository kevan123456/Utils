package com.ws.thread;

public class ThreadLocalTest {

    public static class MyRunnable implements Runnable {

        private A a = new A() ;

        @Override
        public void run() {
            a.setNumber(a.getNumber()+5);
            System.out.println(Thread.currentThread().getName()+":\t"+a.getNumber());
        }
    }

    public static class A{
        private ThreadLocal<Integer> threadLocal = new ThreadLocal<Integer>(){
            @Override
            protected Integer initialValue(){
                return 0;
            }
        } ;
        public int getNumber(){
            return threadLocal.get() ;
        }
        public void setNumber(Integer number){
            threadLocal.set(number) ;
        }
    }

    public static void main(String[] args) {
        MyRunnable sharedRunnableInstance = new MyRunnable();
        Thread thread1 = new Thread(sharedRunnableInstance);
        Thread thread2 = new Thread(sharedRunnableInstance);
        thread1.start();
        thread2.start();
    }
}

