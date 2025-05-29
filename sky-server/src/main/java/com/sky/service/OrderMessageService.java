package com.sky.service;

import com.sky.entity.Orders;

/**
 * 订单消息服务
 */
public interface OrderMessageService {
    
    /**
     * 发送订单状态变更消息
     * @param orders 订单信息
     */
    void sendOrderStatusMessage(Orders orders);
    
    /**
     * 发送订单支付成功消息
     * @param orders 订单信息
     */
    void sendOrderPaymentMessage(Orders orders);
    
    /**
     * 发送订单取消消息
     * @param orders 订单信息
     */
    void sendOrderCancelMessage(Orders orders);
    
    /**
     * 发送订单延时消息（用于超时取消）
     * @param orderId 订单ID
     */
    void sendOrderDelayMessage(Long orderId);
} 