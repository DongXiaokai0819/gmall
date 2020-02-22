package com.atguigu.gmall;

import com.atguigu.gmall.util.RedisUtil;
import redis.clients.jedis.Jedis;

public class TestRedis {
    public static void main(String[] args) {
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.initPool("127.0.0.1",6379,0,"123456");
        Jedis jedis = redisUtil.getJedis();
        jedis.set("test","小凯");

        String test = jedis.get("test");
        System.out.println(test);

    }
}
