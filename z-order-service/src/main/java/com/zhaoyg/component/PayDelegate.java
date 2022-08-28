package com.zhaoyg.component;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * @author zhao
 * @date 2022/8/27
 */
@Component
public class PayDelegate implements ApplicationContextAware {

    private Collection<PayStrategy> payStrategies;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, PayStrategy> beans = applicationContext.getBeansOfType(PayStrategy.class);
        payStrategies = beans.values();
    }

    public String pay(PayInfo payInfo) {
        String payType = payInfo.getPayType();
        for (PayStrategy payStrategy : payStrategies) {
            if (payStrategy.support(payType)) {
                return payStrategy.pay(payInfo);
            }
        }
        throw new UnsupportedOperationException("unsupported pay type " + payType);
    }

    public String payState(String outTradeNo, String payType) {
        for (PayStrategy payStrategy : payStrategies) {
            if (payStrategy.support(payType)) {
                return payStrategy.payState(outTradeNo);
            }
        }
        throw new UnsupportedOperationException("unsupported pay type " + payType);
    }
}
