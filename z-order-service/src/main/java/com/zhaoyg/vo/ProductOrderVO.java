package com.zhaoyg.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhao
 * @date 2022/8/27
 */
@Data
public class ProductOrderVO {

    private Long id;

    @ApiModelProperty("订单唯一标识")
    @JsonProperty("out_trade_no")
    private String outTradeNo;

    @ApiModelProperty("NEW 未支付订单,PAY已经支付订单,CANCEL超时取消订单")
    private String state;

    @ApiModelProperty("订单生成时间")
    @JsonProperty("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty("订单总金额")
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    @ApiModelProperty("订单实际支付价格")
    @JsonProperty("pay_amount")
    private BigDecimal payAmount;

    @ApiModelProperty("支付类型，微信-银行-支付宝")
    @JsonProperty("pay_type")
    private String payType;

    @ApiModelProperty("昵称")
    @JsonProperty("nick_name")
    private String nickname;

    @ApiModelProperty("头像")
    @JsonProperty("head_img")
    private String headImg;

    @ApiModelProperty("用户id")
    @JsonProperty("user_id")
    private Long userId;

    @ApiModelProperty("0表示未删除，1表示已经删除")
    private Integer del;

    @ApiModelProperty("更新时间")
    @JsonProperty("update_time")
    private LocalDateTime updateTime;

    @ApiModelProperty("订单类型 DAILY普通单，PROMOTION促销订单")
    @JsonProperty("order_type")
    private String orderType;

    @ApiModelProperty("收货地址 json存储")
    @JsonProperty("receiver_address")
    private String receiverAddress;

    @ApiModelProperty("订单项")
    @JsonProperty("order_items")
    private List<ProductOrderItemVO> orderItems;
}
