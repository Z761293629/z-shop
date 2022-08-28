package com.zhaoyg.component;

import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.zhaoyg.config.AliPayConfiguration;
import com.zhaoyg.enums.BizCodeEnum;
import com.zhaoyg.enums.ProductOrderPayTypeEnum;
import com.zhaoyg.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author zhao
 * @date 2022/8/27
 */
@Slf4j
@Service
public class AliPayStrategy implements PayStrategy, InitializingBean {

    private AlipayClient alipayClient;

    @Override
    public void afterPropertiesSet() {
        alipayClient = AliPayConfiguration.getInstance();
    }

    @Override
    public String pay(PayInfo payInfo) {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        model.setOutTradeNo(payInfo.getOutTradeNo());
        model.setTotalAmount(payInfo.getTotalAmount());
        model.setSubject(payInfo.getTitle());
        model.setBody(payInfo.getDescription());
        model.setTimeExpire(payInfo.getTimeExpire());
        request.setNotifyUrl(payInfo.getNotifyUrl());
        request.setBizModel(model);
        try {
            AlipayTradePagePayResponse pagePayResponse = alipayClient.pageExecute(request);
            log.info(pagePayResponse.getBody());
            if (pagePayResponse.isSuccess()) {
                return pagePayResponse.getBody();
            }
            log.error("[阿里支付] 申请阿里支付失败 aliResponse=[{}]", JSONUtil.toJsonStr(pagePayResponse));
        } catch (Exception exception) {
            log.error("[阿里支付] 申请阿里支付异常 ", exception);
        }
        throw new BizException(BizCodeEnum.ORDER_ALIPAY_FAIL);
    }

    @Override
    public String payState(String outTradeNo) {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo(outTradeNo);
        request.setBizModel(model);
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            String status = response.getTradeStatus();
            return Objects.equals(status, "TRADE_SUCCESS") ? "TRADE_SUCCESS" : "";
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean support(String payType) {
        return ProductOrderPayTypeEnum.ALI.name().equalsIgnoreCase(payType);
    }


}
