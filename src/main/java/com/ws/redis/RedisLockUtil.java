/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.redis;

public class RedisLockUtil {
    /**
     * redis锁
     * @param cacheService 对应cacheservice
     * @param lockKey   锁的key
     * @param timeOut   锁过期时间 秒
     */
    public static void lock(ICacheService cacheService, String lockKey, int timeOut){
        //返回值0表示有进程或线程获取到锁，自旋锁
        while(cacheService.setnx(lockKey,timeOut,"1") == 0){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }

    }
    /**
     * redis解锁
     * @param cacheService 对应cacheservice
     * @param lockKey   锁的key
     */
    public static void unlock(ICacheService cacheService, String lockKey){
        cacheService.del(new String[]{lockKey});
    }
}
