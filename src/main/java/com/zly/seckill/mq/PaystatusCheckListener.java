package com.zly.seckill.mq;

import com.alibaba.fastjson.JSON;
import com.zly.seckill.db.dao.OrderDao;
import com.zly.seckill.db.dao.SeckillActivityDao;
import com.zly.seckill.db.po.Order;
import com.zly.seckill.services.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RocketMQMessageListener(topic = "pay_check", consumerGroup = "pay_check_group")
public class PaystatusCheckListener implements RocketMQListener<MessageExt> {
    @Resource
    private OrderDao orderDao;
    @Resource
    private SeckillActivityDao seckillActivityDao;
    @Resource
    private RedisService redisService;


    @Override
    public void onMessage(MessageExt messageExt) {
        //1. read message
        String message = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("received the message of pay status check");

        //convert the string message to order class
        Order order = JSON.parseObject(message, Order.class);
        //2. get order info
        Order orderinfo = orderDao.queryOrder(order.getOrderNo());

        //3. check if the order has been paid
        if (orderinfo.getOrderStatus() != 2){
            // if not being paid on time, close the order
            log.info("order is not being paid, close the order: {}", orderinfo.getOrderNo());
            orderinfo.setOrderStatus(99);
            orderDao.updateOrder(orderinfo);
            // restore stock in DB
            seckillActivityDao.revertStock(order.getSeckillActivityId());
            // restore stock in Redis
            redisService.revertStock("Stock:" + order.getSeckillActivityId());
            // remove user from the purchase restriction list, because the current order is failed, the user can purchase again.
            redisService.removeLimitMember(order.getSeckillActivityId(), order.getUserId());
        }
    }
}
