package com.zly.seckill;

import com.zly.seckill.services.RedisService;
import com.zly.seckill.services.SeckillActivityService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.UUID;

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

    /**
     * Test the result of obtaining the lock under high concurrency
     */
    @Test
    public void  testConcurrentAddLock() {
        for (int i = 0; i < 10; i++) {
            String requestId = UUID.randomUUID().toString();
            // Predicted result of print: true false false false false false false false false false
            // Only the first one can obtain the lock
            System.out.println(redisService.tryGetDistributedLock("A", requestId,1000));
        }
    }
}
