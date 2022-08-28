package com.zhaoyg.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhao
 * @date 2022/8/22
 */
@Data
@ApiModel("确认订单请求")
public class ConfirmOrderRequest {

    /**
     * 最终购买的商品列表
     * <p>
     * 传递id，购买数量从购物车中读取
     */
    @JsonProperty("product_ids")
    private List<Long> productIdList;

    /**
     * 优惠券记录id
     */
    @ApiModelProperty("优惠券记录id")
    @JsonProperty("coupon_record_id")
    private Long couponRecordId;

    /**
     * 收货地址id
     */
    @JsonProperty("address_id")
    private Long addressId;


    /**
     * 支付方式
     */
    @JsonProperty("pay_type")
    private String payType;


    /**
     * 端类型
     */
    @JsonProperty("client_type")
    private String clientType;

    /**
     * 总价格，前端传递，后端需要验价
     */
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;


    /**
     * 实际支付的价格，
     * 如果用了优惠劵，则是减去优惠券后端价格，如果没的话，则是totalAmount一样
     */
    @JsonProperty("real_pay_amount")
    private BigDecimal realPayAmount;

    @NotNull(message = "订单确认token不能为空")
    private String token;

}
