package com.sky.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sky.entity.OrderDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel(description = "订单数据传输对象")
public class OrdersDTO implements Serializable {

    @ApiModelProperty(value = "订单主键ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "订单号", example = "202312010001")
    private String number;

    @ApiModelProperty(value = "订单状态 1待付款，2待接单，3已接单，4派送中，5已完成，6已取消", example = "1")
    private Integer status;

    @ApiModelProperty(value = "下单用户ID", example = "100")
    private Long userId;

    @ApiModelProperty(value = "地址簿ID", example = "1")
    private Long addressBookId;

    @ApiModelProperty(value = "下单时间", example = "2023-12-01T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;

    @ApiModelProperty(value = "结账时间", example = "2023-12-01T10:35:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkoutTime;

    @ApiModelProperty(value = "支付方式 1微信，2支付宝", example = "1")
    private Integer payMethod;

    @ApiModelProperty(value = "实收金额", example = "88.50")
    private BigDecimal amount;

    @ApiModelProperty(value = "备注信息", example = "不要辣")
    private String remark;

    @ApiModelProperty(value = "用户名", example = "张三")
    private String userName;

    @ApiModelProperty(value = "手机号", example = "13800138000")
    private String phone;

    @ApiModelProperty(value = "收货地址", example = "北京市朝阳区某某街道")
    private String address;

    @ApiModelProperty(value = "收货人", example = "张三")
    private String consignee;

    @ApiModelProperty(value = "订单详情列表")
    private List<OrderDetail> orderDetails;

}