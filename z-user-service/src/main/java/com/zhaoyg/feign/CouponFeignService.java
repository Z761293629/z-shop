package com.zhaoyg.feign;

import com.zhaoyg.request.NewUserCouponRequest;
import com.zhaoyg.util.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author zhao
 * @date 2022/8/20
 */
@FeignClient("z-coupon-service")
public interface CouponFeignService {

    @PostMapping("/api/coupon/v1/new_user")
    Result getNewUserCoupon(@RequestBody @Validated NewUserCouponRequest newUserCouponRequest);
}
