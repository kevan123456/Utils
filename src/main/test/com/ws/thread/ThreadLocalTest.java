package com.ws.thread;

public class ThreadLocalTest {

    public static class MyRunnable implements Runnable {

        @Override
        public void run() {
            try {
                A.setNumber(A.getNumber()+5);
                System.out.println(Thread.currentThread().getName()+":\t"+A.getNumber());
            }finally {
                //防止出现内存泄露
                A.removeNumber();
            }

        }
    }

    public static class A{
        private static ThreadLocal<Integer> threadLocal = new ThreadLocal<Integer>(){
            @Override
            protected Integer initialValue(){
                return 0;
            }
        } ;
        public static int getNumber(){
            return threadLocal.get() ;
        }
        public static void setNumber(Integer number){
            threadLocal.set(number) ;
        }
        public static void removeNumber(){
            threadLocal.remove();
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

