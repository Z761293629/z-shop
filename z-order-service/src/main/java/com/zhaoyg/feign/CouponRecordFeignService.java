package com.zhaoyg.feign;

import com.zhaoyg.request.LockCouponRequest;
import com.zhaoyg.util.Result;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author zhao
 * @date 2022/8/24
 */
@FeignClient(value = "z-coupon-service", path = "/api/coupon_record/v1")
public interface CouponRecordFeignService {
    @GetMapping("/{record_id}")
    Result detail(
            @ApiParam(name = "record_id", value = "优惠券记录id") @PathVariable(name = "record_id") Long recordId);

    @PostMapping("/lock_records")
    Result lockCouponRecords(@RequestBody LockCouponRequest lockCouponRequest);
}
