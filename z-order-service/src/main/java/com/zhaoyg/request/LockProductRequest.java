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
@ApiModel("锁定商品请求")
public class LockProductRequest {

    @ApiModelProperty(value = "订单子项")
    @JsonProperty("order_items")
    private List<OrderItemRequest> orderItems;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号", example = "ahjlkbjls")
    @JsonProperty("order_trade_out_no")
    private String orderTradeOutNo;

}
