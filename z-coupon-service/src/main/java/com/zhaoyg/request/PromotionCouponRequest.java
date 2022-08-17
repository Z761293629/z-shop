package com.zhaoyg.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhao
 * @date 2022/8/16
 */
@Data
@ApiModel("促销优惠券请求")
public class PromotionCouponRequest {
    @ApiModelProperty("优惠券id")
    @JsonProperty("coupon_id")
    private Long couponId;
}
