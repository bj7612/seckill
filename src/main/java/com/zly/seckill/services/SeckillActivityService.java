package com.zly.seckill.services;

import com.alibaba.fastjson.JSON;
import com.zly.seckill.db.dao.OrderDao;
import com.zly.seckill.db.dao.SeckillActivityDao;
import com.zly.seckill.db.po.Order;
import com.zly.seckill.db.po.SeckillActivity;
import com.zly.seckill.mq.RocketMQService;
import com.zly.seckill.util.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
public class SeckillActivityService {
    @Resource
    private RedisService redisService;
    @Resource
    private SeckillActivityDao seckillActivityDao;
    @Resource
    private RocketMQService rocketMQService;
    @Resource
    private OrderDao orderDao;

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

        // set order creation message throw RocketMq
        rocketMQService.sendMessage("seckill_order", JSON.toJSONString(order));

        /*
         * 3.Send the verification message of the order payment status
         * RocketMQ supports delayed messages, but does not support second-level accuracy. By default, 18 levels of delayed messages are supported.
         * This is universal determined through the messageDelayLevel configuration item on the broker side, as follows:
         * messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
         */
        rocketMQService.sendDelayMessage("pay_check", JSON.toJSONString(order), 6);
        return order;
    }

    /**
     * deal with the order payment has been processed
     * @param orderNo
     */
    public void payOrderProcess(String orderNo) {
        log.info("Order has been payed, oderNo: {}", orderNo);
        Order order = orderDao.queryOrder(orderNo);
        boolean deductStockResult = seckillActivityDao.deductStock(order.getSeckillActivityId());
        if (deductStockResult) {
            order.setPayTime(new Date());
            order.setOrderStatus(2); // 2: payment finished
            orderDao.updateOrder(order);
        }
    }
}
