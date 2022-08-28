package com.zhaoyg.component;

/**
 * @author zhao
 * @date 2022/8/27
 */
public interface PayStrategy {

    String pay(PayInfo payInfo);

    String payState(String outTradeNo);

    boolean support(String payType);


}
