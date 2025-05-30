package com.sky.listener;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.config.RabbitMQConfig;
import com.sky.entity.Orders;
import com.sky.service.OrderMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;



/**
* 订单消息监听器
*/
@Component
@Slf4j
public class OrderMessageListener {

   @Autowired
   private RedisTemplate<Object, Object> redisTemplate;
   @Autowired
   private OrderMessageService orderMessageService;

   /**
    * 监听订单状态变更消息
    */
   private String json2String(int status, String orderName) throws JsonProcessingException {
       Map<String, Object> valueMap = new HashMap<>();
       valueMap.put("status", status);
       valueMap.put("name", orderName);
       ObjectMapper objectMapper = new ObjectMapper();
       return objectMapper.writeValueAsString(valueMap);
   }

   @RabbitListener(queues = RabbitMQConfig.ORDER_STATUS_QUEUE)
   public void handleOrderStatusMessage(String message) {
       try {
           Orders orders = JSON.parseObject(message, Orders.class);
           log.info("接收到订单状态变更消息：订单号={}, 状态={}", orders.getNumber(), orders.getStatus());

           // 更新Redis中的订单状态缓存
           String cacheKey = "order:"  + orders.getId();
              String jsonValue = json2String(orders.getStatus(), orders.getNumber());

           redisTemplate.opsForValue().set(cacheKey, jsonValue);

           // 根据不同状态执行不同逻辑
           switch (orders.getStatus()) {
               case Orders.TO_BE_CONFIRMED:
                   log.info("订单待接单，通知商家");
                   // 可以在这里发送WebSocket消息给商家端
                   break;
               case Orders.CONFIRMED:
                   log.info("订单已接单，通知用户");
                   // 发送消息给用户
                   break;
               case Orders.DELIVERY_IN_PROGRESS:
                   log.info("订单配送中，通知用户");
                   break;
               case Orders.COMPLETED:
                   log.info("订单已完成");
                   break;
               case Orders.CANCELLED:
                   log.info("订单已取消");
                   break;
           }

       } catch (Exception e) {
           log.error("处理订单状态变更消息失败：{}", message, e);
       }
   }

   /**
    * 监听订单支付成功消息
    */
   @RabbitListener(queues = RabbitMQConfig.ORDER_PAYMENT_QUEUE)
   public void handleOrderPaymentMessage(String message) {
       try {
           Orders orders = JSON.parseObject(message, Orders.class);
           log.info("接收到订单支付成功消息：订单号={}", orders.getNumber());

           // 支付成功后的业务处理
           // 1. 发送短信通知用户
           // 2. 通知商家有新订单
           // 3. 更新库存
           // 4. 记录支付日志

           log.info("订单支付成功，开始处理后续业务逻辑");

       } catch (Exception e) {
           log.error("处理订单支付成功消息失败：{}", message, e);
       }
   }

   /**
    * 监听订单取消消息
    */
   @RabbitListener(queues = RabbitMQConfig.ORDER_CANCEL_QUEUE)
   public void handleOrderCancelMessage(String message) {
       try {
           Orders orders = JSON.parseObject(message, Orders.class);
           log.info("接收到订单取消消息：订单号={}", orders.getNumber());

           // 订单取消后的业务处理
           // 1. 恢复库存
           // 2. 处理退款
           // 3. 发送取消通知

           log.info("订单取消，开始处理退款和库存恢复");

       } catch (Exception e) {
           log.error("处理订单取消消息失败：{}", message, e);
       }
   }

   /**
    * 监听订单超时消息（死信队列）
    */
   @RabbitListener(queues = RabbitMQConfig.ORDER_DLX_QUEUE)
   public void handleOrderTimeoutMessage(String orderId) {
       try {
           log.info("接收到订单超时消息：订单ID={}", orderId);

           // 处理订单超时逻辑
           // 1. 检查订单状态
           // 2. 如果仍未支付，则自动取消订单
           // 3. 恢复库存
           // 4. 发送取消通知

           log.info("订单超时，自动取消订单：{}", orderId);

       } catch (Exception e) {
           log.error("处理订单超时消息失败：订单ID={}", orderId, e);
       }
   }
}