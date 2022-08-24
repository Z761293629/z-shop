package com.zhaoyg.feign.factory;

import com.zhaoyg.feign.ProductOrderFeignService;
import com.zhaoyg.feign.fallback.ProductOrderFeignServiceFallback;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author zhao
 * @date 2022/8/21
 */
@Component
public class ProductOrderFeignServiceFactory implements FallbackFactory<ProductOrderFeignService> {
    @Override
    public ProductOrderFeignService create(Throwable throwable) {
        ProductOrderFeignServiceFallback productOrderFeignServiceFallback = new ProductOrderFeignServiceFallback();
        productOrderFeignServiceFallback.setThrowable(throwable);
        return productOrderFeignServiceFallback;
    }
}
