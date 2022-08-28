package com.zhaoyg.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zhao
 * @date 2022/8/21
 */
@Data
@ApiModel("锁定优惠券请求")
public class LockCouponRequest {

    @ApiModelProperty(value = "优惠券记录ids", example = "[1,2,3]")
    @JsonProperty("coupon_record_ids")
    private List<Long> couponRecordIds;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号", example = "ahjlkbjls")
    @JsonProperty("order_out_trade_no")
    private String orderOutTradeNo;

}
