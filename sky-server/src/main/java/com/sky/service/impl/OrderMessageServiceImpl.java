package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.sky.config.RabbitMQConfig;
import com.sky.entity.Orders;
import com.sky.service.OrderMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 订单消息服务实现类
 */
@Service
@Slf4j
public class OrderMessageServiceImpl implements OrderMessageService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void sendOrderStatusMessage(Orders orders) {
        try {
            String message = JSON.toJSONString(orders);
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_STATUS_ROUTING_KEY,
                message
            );
            log.info("发送订单状态变更消息成功，订单号：{}, 状态：{}", orders.getNumber(), orders.getStatus());
        } catch (Exception e) {
            log.error("发送订单状态变更消息失败，订单号：{}", orders.getNumber(), e);
        }
    }

    @Override
    public void sendOrderPaymentMessage(Orders orders) {
        try {
            String message = JSON.toJSONString(orders);
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_PAYMENT_ROUTING_KEY,
                message
            );
            log.info("发送订单支付成功消息成功，订单号：{}", orders.getNumber());
        } catch (Exception e) {
            log.error("发送订单支付成功消息失败，订单号：{}", orders.getNumber(), e);
        }
    }

    @Override
    public void sendOrderCancelMessage(Orders orders) {
        try {
            String message = JSON.toJSONString(orders);
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_CANCEL_ROUTING_KEY,
                message
            );
            log.info("发送订单取消消息成功，订单号：{}", orders.getNumber());
        } catch (Exception e) {
            log.error("发送订单取消消息失败，订单号：{}", orders.getNumber(), e);
        }
    }

    @Override
    public void sendOrderDelayMessage(Long orderId) {
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_DELAY_EXCHANGE,
                RabbitMQConfig.ORDER_DELAY_ROUTING_KEY,
                orderId.toString()
            );
            log.info("发送订单延时消息成功，订单ID：{}", orderId);
        } catch (Exception e) {
            log.error("发送订单延时消息失败，订单ID：{}", orderId, e);
        }
    }
} 