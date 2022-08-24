package com.zhaoyg.feign;

import com.zhaoyg.util.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author zhao
 * @date 2022/8/22
 */
@FeignClient(value = "z-user-service", path = "/api/address/v1/detail")
public interface UserAddressFeignService {
    @GetMapping("/{address_id}")
    Result detail(@PathVariable("address_id") Long addressId);
}
