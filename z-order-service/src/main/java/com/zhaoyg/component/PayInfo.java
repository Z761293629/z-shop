package com.zhaoyg.component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

/**
 * @author zhao
 * @date 2022/8/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayInfo {
    private String outTradeNo;
    private String payType;
    private String totalAmount;
    private String title;
    private String description;
    private String clientType;
    private String timeExpire;
    private String notifyUrl;
}
