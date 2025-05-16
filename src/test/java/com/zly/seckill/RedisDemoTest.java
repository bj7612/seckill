package com.zly.seckill;

import com.zly.seckill.services.RedisService;
import com.zly.seckill.services.SeckillActivityService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class RedisDemoTest {
    @Resource
    private RedisService redisService;
    @Resource
    private SeckillActivityService seckillActivityService;

    @Test
    public void setStockTest() {
        redisService.setValue("stock 19", 10L);
    }

    // Test if the redis load the activity data from mySql success
    @Test
    public void getStockTest() {
        String stock =redisService.getValue("stock:28");
        System.out.println("the value of key: stock 28 is" + stock);
    }

    // Test the pushSeckillInfoToRedis
    @Test
    public void pushSeckillInfoToRedisTest(){
        seckillActivityService.pushSeckillInfoToRedis(19);
    }
}
