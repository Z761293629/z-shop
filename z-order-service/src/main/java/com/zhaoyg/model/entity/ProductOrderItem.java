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
@TableName("product_order_item")
@ApiModel(value = "ProductOrderItem对象", description = "")
public class ProductOrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("订单号")
    private Long productOrderId;

    private String outTradeNo;

    @ApiModelProperty("产品id")
    private Long productId;

    @ApiModelProperty("商品名称")
    private String productName;

    @ApiModelProperty("商品图片")
    private String productImg;

    @ApiModelProperty("购买数量")
    private Integer buyNum;

    private LocalDateTime createTime;

    @ApiModelProperty("购物项商品总价格")
    private BigDecimal totalAmount;

    @ApiModelProperty("购物项商品单价")
    private BigDecimal amount;


}
