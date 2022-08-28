package com.zhaoyg.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayConstants;
import com.alipay.api.internal.util.AlipaySignature;
import com.zhaoyg.config.AliPayConfiguration;
import com.zhaoyg.enums.ProductOrderPayTypeEnum;
import com.zhaoyg.service.ProductOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhao
 * @date 2022/8/27
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/callback/v1")
public class CallbackController {
    private final ProductOrderService productOrderService;

    @PostMapping("/ali")
    public void aliCallback(HttpServletRequest request) {

        Map<String, String[]> parameterMap = request.getParameterMap();
        log.info("ali callback {}", parameterMap);
        if (parameterMap.size() == 0) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            if (entry.getValue().length == 1) {
                map.put(entry.getKey(), entry.getValue()[0]);
            }
        }
        try {
            boolean verify = AlipaySignature.rsaCheckV1(map, AliPayConfiguration.ALI_PAY_PUBLIC_KEY, AlipayConstants.CHARSET_UTF8, AlipayConstants.SIGN_TYPE_RSA2);
            if (verify) {
                productOrderService.handlePayCallback(ProductOrderPayTypeEnum.ALI, map);
                return;
            }
            log.warn("[ALI CALLBACK] 验签失败");
        } catch (AlipayApiException e) {
            log.warn("[ALI CALLBACK] 验签异常", e);
        }
    }

}
