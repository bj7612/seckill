package com.zly.seckill.mq;

import com.alibaba.fastjson.JSON;
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

@Slf4j
@Component
@Transactional
@RocketMQMessageListener(topic = "pay_done", consumerGroup = "pay_done_group")
public class PayDoneConsumer implements RocketMQListener<MessageExt> {
    @Resource
    private SeckillActivityDao seckillActivityDao;

    @Override
    public void onMessage(MessageExt messageExt) {
        //1. parse the message to class Order
        String message = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("Received the pay done message: {}", message);
        Order order = JSON.parseObject(message, Order.class);

        //2. deduct the stock
        seckillActivityDao.deductStock(order.getSeckillActivityId());
    }
}
