package com.zhaoyg.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * <p>
 *
 * </p>
 *
 * @author zhao
 * @since 2022-08-14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("coupon_record")
@ApiModel(value = "CouponRecord对象", description = "")
public class CouponRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("优惠券id")
    private Long couponId;

    @ApiModelProperty("创建时间获得时间")
    private LocalDateTime createTime;

    @ApiModelProperty("使用状态  可用 NEW,已使用USED,过期 EXPIRED;")
    private String useState;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("用户昵称")
    private String userName;

    @ApiModelProperty("优惠券标题")
    private String couponTitle;

    @ApiModelProperty("开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty("结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty("订单id")
    private Long orderId;

    @ApiModelProperty("抵扣价格")
    private BigDecimal price;

    @ApiModelProperty("满多少才可以使用")
    private BigDecimal conditionPrice;


}
