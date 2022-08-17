package com.zhaoyg.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

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
 * @since 2022-08-14
 */
@Getter
@Setter
@ApiModel(value = "Coupon对象", description = "")
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("优惠卷类型[NEW_USER注册赠券，TASK任务卷，PROMOTION促销劵]")
    private String category;

    @ApiModelProperty("发布状态, PUBLISH发布，DRAFT草稿，OFFLINE下线")
    private String publish;

    @ApiModelProperty("优惠券图片")
    private String couponImg;

    @ApiModelProperty("优惠券标题")
    private String couponTitle;

    @ApiModelProperty("抵扣价格")
    private BigDecimal price;

    @ApiModelProperty("每人限制张数")
    private Integer userLimit;

    @ApiModelProperty("优惠券开始有效时间")
    private LocalDateTime startTime;

    @ApiModelProperty("优惠券失效时间")
    private LocalDateTime endTime;

    @ApiModelProperty("优惠券总量")
    private Integer publishCount;

    @ApiModelProperty("库存")
    private Integer stock;

    private LocalDateTime createTime;

    @ApiModelProperty("满多少才可以使用")
    private BigDecimal conditionPrice;


}
