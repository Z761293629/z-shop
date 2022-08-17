package com.zhaoyg.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author zhao
 * @date 2022/8/14
 */

@Data
@ApiModel("优惠券VO")
public class CouponVO {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("优惠卷类型[NEW_USER注册赠券，TASK任务卷，PROMOTION促销劵]")
    private String category;

    @ApiModelProperty("优惠券图片")
    @JsonProperty("coupon_img")
    private String couponImg;

    @ApiModelProperty("优惠券标题")
    @JsonProperty("coupon_title")
    private String couponTitle;

    @ApiModelProperty("抵扣价格")
    private BigDecimal price;

    @ApiModelProperty("每人限制张数")
    @JsonProperty("user_limit")
    private Integer userLimit;

    @ApiModelProperty("优惠券开始有效时间")
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", locale = "zh", timezone = "GMT+8")
    @JsonProperty("start_time")
    private LocalDateTime startTime;

    @ApiModelProperty("优惠券失效时间")
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", locale = "zh", timezone = "GMT+8")
    @JsonProperty("end_time")
    private LocalDateTime endTime;

    @ApiModelProperty("优惠券总量")
    @JsonProperty("publish_count")
    private Integer publishCount;

    @ApiModelProperty("库存")
    private Integer stock;


    @ApiModelProperty("满多少才可以使用")
    @JsonProperty("condition_price")
    private BigDecimal conditionPrice;
}
