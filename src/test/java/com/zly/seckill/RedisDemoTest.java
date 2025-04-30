package com.zly.seckill;

import com.zly.seckill.services.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class RedisDemoTest {
    @Resource
    private RedisService redisService;

    @Test
    public void setStockTest() {
        redisService.setValue("stock 19", 10L);
    }

    @Test
    public void getStockTest() {
        String stock =redisService.getValue("stock 19");
        System.out.println("the value of key: stock 12 is" + stock);
    }
}
