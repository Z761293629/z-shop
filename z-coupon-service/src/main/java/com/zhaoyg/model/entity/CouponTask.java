package com.zhaoyg.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
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
 * @since 2022-08-21
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("coupon_task")
@ApiModel(value = "CouponTask对象", description = "")
public class CouponTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("优惠券记录id")
    private Long couponRecordId;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("订单号")
    private String outTradeNo;

    @ApiModelProperty("锁定状态 锁定LOCK-完成FINISH 取消CANCEL")
    private String lockState;


}
