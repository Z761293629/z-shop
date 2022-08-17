package com.zhaoyg.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author zhao
 * @date 2022/8/16
 */
@Data
public class CouponRecordVO {

    private Long id;

    @ApiModelProperty("优惠券id")
    @JsonProperty("coupon_id")
    private Long couponId;

    @ApiModelProperty("使用状态  可用 NEW,已使用USED,过期 EXPIRED;")
    @JsonProperty("use_state")
    private String useState;

    @ApiModelProperty("用户昵称")
    @JsonProperty("user_name")
    private String userName;

    @ApiModelProperty("优惠券标题")
    @JsonProperty("coupon_title")
    private String couponTitle;

    @ApiModelProperty("开始时间")
    @JsonProperty("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8", locale = "zh")
    private LocalDateTime startTime;

    @ApiModelProperty("结束时间")
    @JsonProperty("end_time")
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8", locale = "zh")
    private LocalDateTime endTime;

    @ApiModelProperty("订单id")
    @JsonProperty("order_id")
    private Long orderId;

    @ApiModelProperty("抵扣价格")
    private BigDecimal price;

    @ApiModelProperty("满多少才可以使用")
    @JsonProperty("condition_price")
    private BigDecimal conditionPrice;
}
