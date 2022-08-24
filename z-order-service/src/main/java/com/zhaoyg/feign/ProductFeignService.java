package com.zhaoyg.feign;

import com.zhaoyg.request.LockProductRequest;
import com.zhaoyg.util.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author zhao
 * @date 2022/8/24
 */
@FeignClient(value = "z-product-service", contextId = "product", path = "/api/product/v1")
public interface ProductFeignService {
    @PostMapping("/lock_products")
    Result lockProducts(@RequestBody LockProductRequest lockProductRequest);
}
