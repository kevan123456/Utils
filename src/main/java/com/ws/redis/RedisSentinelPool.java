package com.ws.redis;


import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.JedisSentinelPool;

import java.util.Set;

public class RedisSentinelPool {

    private static JedisSentinelPool pool = null;

    public static synchronized JedisSentinelPool getSentinelPool(String masterName, Set<String> sentinels) {

        if (pool == null) {

            GenericObjectPoolConfig config = new GenericObjectPoolConfig();

            // 设置最大的空闲实例数
            config.setMaxIdle(20);
            // 设置最小的空闲实例数
            config.setMinIdle(10);
            config.setMaxTotal(200);
            config.setTimeBetweenEvictionRunsMillis(1000);
            // 最大等待时间（毫秒）
            config.setMaxWaitMillis(30000);
            //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
            config.setTestOnBorrow(true);

            pool = new JedisSentinelPool(masterName, sentinels, config);
        }

        return pool;

    }
}

