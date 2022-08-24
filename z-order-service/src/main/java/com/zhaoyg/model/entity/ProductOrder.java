package com.zhaoyg.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author zhao
 * @since 2022-08-20
 */
@Getter
@Setter
@TableName("product_order")
@ApiModel(value = "ProductOrder对象", description = "")
public class ProductOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("订单唯一标识")
    private String outTradeNo;

    @ApiModelProperty("NEW 未支付订单,PAY已经支付订单,CANCEL超时取消订单")
    private String state;

    @ApiModelProperty("订单生成时间")
    private LocalDateTime createTime;

    @ApiModelProperty("订单总金额")
    private BigDecimal totalAmount;

    @ApiModelProperty("订单实际支付价格")
    private BigDecimal payAmount;

    @ApiModelProperty("支付类型，微信-银行-支付宝")
    private String payType;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("头像")
    private String headImg;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("0表示未删除，1表示已经删除")
    private Integer del;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("订单类型 DAILY普通单，PROMOTION促销订单")
    private String orderType;

    @ApiModelProperty("收货地址 json存储")
    private String receiverAddress;


}
