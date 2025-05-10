package com.zly.seckill.mq;

import com.alibaba.fastjson.JSON;
import com.zly.seckill.db.dao.OrderDao;
import com.zly.seckill.db.dao.SeckillActivityDao;
import com.zly.seckill.db.po.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
@RocketMQMessageListener(topic = "seckill_order", consumerGroup = "seckill_order_group")
public class OrderConsumer implements RocketMQListener<MessageExt> {
    @Resource
    private OrderDao orderDao;

    @Resource
    private SeckillActivityDao seckillActivityDao;

    @Override
    @Transactional
    public void onMessage (MessageExt messageExt) {
        //1.Parse the request message for creating the order
        String message = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("Received the request message for creating the order：{}", message);
        // deserialization of message
        Order order = JSON.parseObject(message, Order.class);
        order.setCreateTime(new Date());

        //2.Deduct available and Lock inventory
        boolean lockStockResult =
                seckillActivityDao.lockStock(order.getSeckillActivityId());
        if (lockStockResult) {
            //Set order status: 0:No available inventory,invalid order; 1:Created and waiting for payment.
            order.setOrderStatus(1);
        } else {
            order.setOrderStatus(0);
        }
        //3.Insert an order to DB.
        orderDao.insertOrder(order);
    }

}
