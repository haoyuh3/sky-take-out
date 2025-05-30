package com.sky.controller.admin;

import com.sky.dto.OrdersDTO;
import com.sky.entity.Orders;
import com.sky.result.Result;
import com.sky.service.OrderMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/order")
@Api(tags = "点餐接口")
@Slf4j
public class OrderController {

    @Autowired
    private OrderMessageService orderMessageService;

    @PostMapping("/status")
    @ApiOperation("订单状态变更")
    public Result<String> orderStatus(@RequestBody OrdersDTO ordersDTO) {
        String orderNumber = ordersDTO.getNumber();
        Integer status = ordersDTO.getStatus();
        Long orderId = ordersDTO.getId();
        log.info("测试订单状态变更：订单号={}, 状态={}, 订单ID={}", orderNumber, status, orderId);

        Orders currentOrder = Orders.builder()
                .id(orderId)
                .number(orderNumber)
                .status(status)
                .amount(new BigDecimal("35.50"))
                .orderTime(LocalDateTime.now())
                .build();
        orderMessageService.sendOrderStatusMessage(currentOrder);
        return Result.success("订单状态变更消息发送成功！订单ID: " + orderId + "，请查看控制台日志");
    }

    @PostMapping("payment")
    @ApiOperation("支付成功消息")
    public Result<String> PaymentSuccess(@RequestBody OrdersDTO ordersDTO) {
        Long orderId = ordersDTO.getId();
        String orderNumber = ordersDTO.getNumber();
        log.info("支付成功：订单号={}, 订单ID={}", orderNumber, orderId);


        Orders currentOrder = Orders.builder()
                .id(orderId)
                .number(orderNumber)
                .status(Orders.PAID)
                .amount(new BigDecimal("35.50"))
                .orderTime(LocalDateTime.now())
                .build();
        orderMessageService.sendOrderPaymentMessage(currentOrder);
        return Result.success("支付成功消息发送成功！订单ID: " + orderId + "，请查看控制台日志");
    }

    @PostMapping("cancel")
    @ApiOperation("订单取消消息")
    public Result<String> cancelOrder(@RequestBody OrdersDTO ordersDTO) {
        String orderNumber = ordersDTO.getNumber();
        Long orderId = ordersDTO.getId();
        log.info("订单取消：订单号={}, 订单ID={}", orderNumber, orderId);

        Orders currentOrder = Orders.builder()
                .id(orderId)
                .number(orderNumber)
                .status(Orders.CANCELLED)
                .amount(new BigDecimal("35.50"))
                .orderTime(LocalDateTime.now())
                .build();
        orderMessageService.sendOrderCancelMessage(currentOrder);
        return Result.success("订单取消消息发送成功！订单ID: " + orderId + "，请查看控制台日志");
    }

    @PostMapping("delay")
    @ApiOperation("延时消息")
    public Result<String> testDelayMessage(@RequestBody Long orderId) {

        log.info("延时消息：订单ID={}", orderId);

        // 🚀 发送延时消息（15分钟后处理）
        orderMessageService.sendOrderDelayMessage(orderId);

        return Result.success("延时消息发送成功！15分钟后将自动处理");
    }



}
