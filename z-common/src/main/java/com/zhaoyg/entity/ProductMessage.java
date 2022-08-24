package com.zhaoyg.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhao
 * @date 2022/8/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductMessage {

    private Long productTaskId;

    private String orderTradeOutNo;

}
