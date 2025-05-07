package com.zly.seckill.services;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisServiceTest {
    @Resource
    private RedisService redisService;

    // test the redis function set get deduct
    @Test
    void setValue() {
         redisService.setValue("test:1", 100L);
        String value = redisService.getValue("test:1");
        assertEquals(100L, Long.parseLong(value));
    }

    @Test
    void getValue() {
        String value = redisService.getValue("test:1");
        assertEquals(100L, Long.parseLong(value));
    }

    @Test
    void stockDeductValidator() {
        boolean result = redisService.stockDeductValidator("test:1");
        System.out.println("result:"+result);
        assertTrue(result);
        String stock = redisService.getValue("test:1");
        System.out.println("test:1"+stock);
        assertEquals("99", stock);

    }
}