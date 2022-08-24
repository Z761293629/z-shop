package com.zhaoyg.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author zhao
 * @date 2022/8/20
 */
@Data
@ApiModel("购物车商品项修改请求")
public class CartItemUpdateRequest {

    @ApiModelProperty("商品id")
    @JsonProperty("product_id")
    @NotNull(message = "商品id不能为空")
    private Long productId;

    @ApiModelProperty("购买数量")
    @JsonProperty("buy_num")
    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量不能小于1")
    private Integer buyNum;

}
