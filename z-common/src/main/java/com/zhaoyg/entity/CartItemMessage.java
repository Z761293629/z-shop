package com.zhaoyg.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhao
 * @date 2022/8/22
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemMessage {
    private Long userId;
    private String orderOutTradeNo;
    private Long productId;
    private Integer buyNum;
}
