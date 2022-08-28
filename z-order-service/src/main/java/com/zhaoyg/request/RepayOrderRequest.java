package com.zhaoyg.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author zhao
 * @date 2022/8/27
 */
@Data
public class RepayOrderRequest {
    @JsonProperty("out_trade_no")
    private String outTradeNo;
    /**
     * 支付方式
     */
    @JsonProperty("pay_type")
    private String payType;
    /**
     * 端类型
     */
    @JsonProperty("client_type")
    private String clientType;
}
