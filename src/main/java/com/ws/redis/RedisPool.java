package com.ws.redis;


import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisPool {

    private static Map<String, JedisPool> poolMap = new ConcurrentHashMap<>();


    public static synchronized JedisPool getPool(String ip, int port) {
        String poolKey = ip + ":" + port;


        if (!poolMap.containsKey(poolKey)) {
            JedisPoolConfig config = new JedisPoolConfig();

            //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
            config.setMaxIdle(20);
            config.setMinIdle(10);
            config.setMaxTotal(200);
            config.setTimeBetweenEvictionRunsMillis(1000);

            //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；

            config.setTestOnBorrow(true);

            JedisPool pool = new JedisPool(config, ip, port);

            poolMap.put(poolKey, pool);
        }


        return poolMap.get(poolKey);

    }

    /**
     * @param ip redis地址
     * @param port 端口
     * @param minIdle   最小空闲连接数量
     * @param maxIdle   最大空闲连接数量
     * @param maxTotal  最大连接数量
     * @return
     */
    public static synchronized JedisPool getPool(String ip, int port,int minIdle, int maxIdle, int maxTotal) {
        String poolKey = ip + ":" + port;


        if (!poolMap.containsKey(poolKey)) {
            JedisPoolConfig config = new JedisPoolConfig();

            //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
            config.setMaxIdle(maxIdle);
            config.setMinIdle(minIdle);
            config.setMaxTotal(maxTotal);
            config.setTimeBetweenEvictionRunsMillis(1000);

            //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；

            config.setTestOnBorrow(true);

            JedisPool pool = new JedisPool(config, ip, port);

            poolMap.put(poolKey, pool);
        }


        return poolMap.get(poolKey);
    }
}
