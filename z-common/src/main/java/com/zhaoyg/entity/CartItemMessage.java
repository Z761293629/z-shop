package com.zhaoyg.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
    private String orderTradeOutNo;
    private Long productId;
    private Integer buyNum;
}
