package com.zly.seckill.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;


@Configuration
public class JedisConfig extends JedisPoolConfig {
    private final Logger logger = LoggerFactory.getLogger(JedisConfig.class);
    @Value("${spring.data.redis.host}") //set host = localhost or you can get the value from application.properties
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;
    @Value("${spring.data.redis.timeout}") // get timeout value from application.properties
    private int timeout;
    @Value("${spring.data.redis.jedis.pool.max-active}")
    private int maxActive;
    @Value("${spring.data.redis.jedis.pool.max-idle}")
    private int maxIdle;
    @Value("${spring.data.redis.jedis.pool.min-idle}")
    private int minIdle;
    @Value("${spring.data.redis.jedis.pool.max-wait}")
    private long maxWaitMillis;

    // 完成属性注入，向 Spring 容器注入 JedisPool
    @Bean
    public JedisPool redisPoolFactory(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(this.maxIdle);
        jedisPoolConfig.setMaxWait(Duration.ofMillis(this.maxWaitMillis));
        jedisPoolConfig.setMaxTotal(this.maxActive);
        jedisPoolConfig.setMinIdle(this.minIdle);
        // 重点是这一行，禁止注册MBean
        jedisPoolConfig.setJmxEnabled(false);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig,host,port,timeout,null);
        logger.info("JedisPool inject success！");
        logger.info("redis address：{}:{}", host, port);
        return jedisPool;
    }
}
