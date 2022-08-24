package com.zhaoyg.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author zhao
 * @date 2022/8/21
 */
@Data
@ApiModel("订单商品子项")
public class OrderItemRequest {

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("buy_num")
    private Integer buyNum;


}
