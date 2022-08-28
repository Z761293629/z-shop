package com.zhaoyg.feign.fallback;

import com.zhaoyg.feign.ProductOrderFeignService;
import com.zhaoyg.util.Result;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * @author zhao
 * @date 2022/8/21
 */
@Component
public class ProductOrderFeignServiceFallback implements ProductOrderFeignService {

    @Setter
    private Throwable throwable;


    @Override
    public Result queryProductOrderState(String orderOutTradeNo) {
        return Result.build(-1, throwable.getMessage());
    }
}
