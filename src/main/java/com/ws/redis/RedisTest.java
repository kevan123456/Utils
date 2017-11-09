package com.ws.redis;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class RedisTest extends TestCase {


    @Test
    public void testPut(){
        RedisService redisService = new RedisService();
        redisService.setIp("10.1.21.76");
        redisService.setPort(7091);
        redisService.setDatabase(26);
        redisService.init();
        //redisService.set("test","test001");
        Map<String,String> map = new HashMap<>() ;
        map.put("ww","111") ;
        redisService.hmset("112",map);

        /*List<String> list = new ArrayList<>() ;
        list.add("11");
        list.add("2");
        list.add("3");
        String[] arr = list.toArray(new String[list.size()]) ;
        redisService.lpush("keykey",arr);*/
    }

    @Test
    public void test() throws InterruptedException {
        Thread t;

        int threadNum = 2;

        final CountDownLatch countDownLatch = new CountDownLatch(threadNum);

        final CountDownLatch runningThreadNum = new CountDownLatch(threadNum);

//        final Set<JedisPool> jedisPools = SynchronizedSet.decorate(new HashSet<JedisPool>());

        final RedisService redisService = new RedisService();
        redisService.setIp("10.1.21.76");
        redisService.setPort(7091);



        long start = System.currentTimeMillis();
        final Integer[] integer = {new Integer(0)};
        for(int i = 0 ; i < threadNum ; i++){
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

//                    jedisPools.add(RedisPool.getPool("10.1.6.20",7091));


//                    Assert.isTrue(jedisPools.size() == 1, "redis has more instance");
                    RedisLockUtil.lock(redisService, "a",100);
                    integer[0] +=1;
                    RedisLockUtil.unlock(redisService,"a");

                    runningThreadNum.countDown();
                }
            });

            t.start();

            countDownLatch.countDown();
        }



        runningThreadNum.await();

        long end = System.currentTimeMillis();


        System.out.println(end-start);

        System.out.println("data : " + integer[0]);
//        long start = System.currentTimeMillis();
//        for(int i = 0 ; i < threadNum ; i++){
//            t = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        countDownLatch.await();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
////                    jedisPools.add(RedisPool.getPool("10.1.6.20",7091));
//                    redisService.set("abc","abc");
//                    runningThreadNum.countDown();
////                    Assert.isTrue(jedisPools.size() == 1, "redis has more instance");
//                }
//            });
//
//            t.start();
//
//            countDownLatch.countDown();
//        }
//
//
//
//        runningThreadNum.await();
//
//        long end = System.currentTimeMillis();
//
//
//        System.out.println(end-start);

    }
}

