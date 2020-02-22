package com.atguigu.gmall.conf;

import com.atguigu.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConf {
    //读取配置文件中的redis的ip地址
    @Value("${spring.redis.host:disabled}")
    private String host;
    @Value("${spring.redis.port:0}")
    private int port ;
    @Value("${spring.redis.database:0}")
    private int database;
    @Value("${spring.redis.password:123456}")
    private String password;
    @Bean
    public RedisUtil getRedisUtil(){
        if(host.equals("disabled")){
            return null;
        }
        RedisUtil redisUtil=new RedisUtil();
        redisUtil.initPool(host,port,database,password);
        redisUtil.initPool("127.0.0.1",6379,0,"123456");
        return redisUtil;
    }
}
