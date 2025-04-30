package com.zly.seckill.services;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SeckillActivityService {
    @Resource
    private RedisService redisService;

/**
 * 判断商品是否还有库存
 * @param activityId 商品ID
 * @return boolean
 */
public boolean seckillStockValidator(Long activityId) {
        String key = "stock:" + activityId;
        // System.out.println("Redis key: " + key);
        return redisService.stockDeductValidator(key);
    }
}
