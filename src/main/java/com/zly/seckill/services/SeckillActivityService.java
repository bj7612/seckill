package com.zly.seckill.services;

import com.alibaba.fastjson.JSON;
import com.zly.seckill.db.dao.SeckillActivityDao;
import com.zly.seckill.db.po.Order;
import com.zly.seckill.db.po.SeckillActivity;
import com.zly.seckill.mq.RocketMQService;
import com.zly.seckill.util.SnowFlake;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SeckillActivityService {
    @Resource
    private RedisService redisService;

    @Resource
    private SeckillActivityDao seckillActivityDao;

    @Resource
    private RocketMQService rocketMQService;

    /**
     * datacenterId; 数据中心, machineId; 机器标识
     * In a distributed environment, it is possible to read from the machine configuration.
     * In a single-machine development environment, it is first written down
     */
    private SnowFlake snowFlake = new SnowFlake(1,1);


    /**
    * Determine whether the goods are still in stock
    * @param activityId  actiivity ID
    * @return boolean
    */
    public boolean seckillStockValidator(Long activityId) {
        String key = "stock:" + activityId;
        // System.out.println("Redis key: " + key);
        return redisService.stockDeductValidator(key);
    }

    /**
     * create order
     * @param seckillActivityId
     * @param userId
     * @return
     * @throws Exception
     */

    public Order createOrder(long seckillActivityId, long userId) throws Exception {
        SeckillActivity activity = seckillActivityDao.querySeckillActivityById(seckillActivityId);
        Order order = new Order();
        order.setOrderNo(String.valueOf(snowFlake.nextId()));
        order.setSeckillActivityId(activity.getId());
        order.setUserId(userId);
        order.setOrderAmount(activity.getSeckillPrice().longValue());

        // set order message throw RocketMq
        rocketMQService.sendMessage("seckill_order", JSON.toJSONString(order));
        return order;
    }
}
