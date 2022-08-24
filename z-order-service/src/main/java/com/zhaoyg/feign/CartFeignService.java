package com.zhaoyg.feign;

import com.zhaoyg.util.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author zhao
 * @date 2022/8/22
 */
@FeignClient(value = "z-product-service", contextId = "cart", path = "/api/cart/v1")
public interface CartFeignService {

    @PostMapping("/confirm_order_cart_items")
    Result confirmOrderCartItems(@RequestBody List<Long> productIds, @RequestParam("order_trade_out_no") String orderTradeOutNo);

}
