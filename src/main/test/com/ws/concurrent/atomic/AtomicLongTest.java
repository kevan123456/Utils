package com.ws.concurrent.atomic;

import junit.framework.TestCase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicLongTest extends TestCase{

    public void testGetAndIncrement(){
        int threadCount = 10 ;
        final int loopCount = 1000 ;
        NoSafeSeq noSafeSeq = new NoSafeSeq() ;
        SafeSeq safeSeq = new SafeSeq() ;
        //等待子线程结束
        CountDownLatch latch = new CountDownLatch(threadCount) ;
        for(int i=0;i<threadCount;i++){
            int index = i ;
            new Thread(){
                @Override
                public void run(){
                    for(int j=0;j<loopCount;j++){
                        noSafeSeq.inc();
                        safeSeq.inc();
                    }
                    System.out.println("finish:"+index) ;
                    latch.countDown();
                }
            }.start();
        }
        //等待子线程结束,打印值
        try {
            latch.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println("noSafeSeq="+noSafeSeq.getCount()) ;
        System.out.println("safeSeq="+safeSeq.getCount()) ;
    }

    class NoSafeSeq{
        private long count = 0 ;
        public void inc(){
            count++ ;
        }
        public long getCount(){
            return count ;
        }
    }

    class SafeSeq{
        private AtomicLong count = new AtomicLong(0) ;
        public void inc(){
            count.getAndIncrement() ;
        }
        public long getCount(){
            return count.longValue() ;
        }
    }
}
