package com.zhaoyg.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author zhao
 * @since 2022-08-17
 */
@Data
@ApiModel(value = "Product对象")
public class ProductVO implements Serializable {


    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("封面图")
    @JsonProperty("cover_img")
    private String coverImg;

    @ApiModelProperty("详情")
    private String detail;

    @ApiModelProperty("老价格")
    @JsonProperty("old_amount")
    private BigDecimal oldAmount;

    @ApiModelProperty("新价格")
    private BigDecimal amount;

    @ApiModelProperty("库存")
    private Integer stock;

}
