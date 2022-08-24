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
 * @since 2022-08-17
 */
@Getter
@Setter
@ApiModel(value = "Product对象")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("封面图")
    private String coverImg;

    @ApiModelProperty("详情")
    private String detail;

    @ApiModelProperty("老价格")
    private BigDecimal oldAmount;

    @ApiModelProperty("新价格")
    private BigDecimal amount;

    @ApiModelProperty("库存")
    private Integer stock;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("锁定库存")
    private Integer lockStock;


}
