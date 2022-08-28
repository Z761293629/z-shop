package com.zhaoyg.feign;

import com.zhaoyg.feign.factory.ProductOrderFeignServiceFactory;
import com.zhaoyg.util.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zhao
 * @date 2022/8/21
 */
@FeignClient(value = "z-order-service", fallbackFactory = ProductOrderFeignServiceFactory.class)
public interface ProductOrderFeignService {

    @GetMapping("/api/order/v1/state")
    Result queryProductOrderState(@RequestParam("order_out_trade_no") String orderOutTradeNo);
}
