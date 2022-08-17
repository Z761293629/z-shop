package com.zhaoyg.controller;


import com.zhaoyg.request.AddressAddRequest;
import com.zhaoyg.service.AddressService;
import com.zhaoyg.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 电商-公司收发货地址表 前端控制器
 * </p>
 *
 * @author zhao
 * @since 2022-08-11
 */
@Api(tags = "收货地址模块")
@RestController
@RequestMapping("/api/address/v1")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping()
    @ApiOperation("新增收货地址")
    public Result addAddress(@RequestBody AddressAddRequest addressAddRequest) {
        addressService.addAddress(addressAddRequest);
        return Result.success();
    }

    @ApiOperation("删除收货地址")
    @DeleteMapping("{address_id}")
    public Result delete(
            @ApiParam(name = "address_id", value = "地址id") @PathVariable("address_id") Long addressId) {
        return addressService.delete(addressId);
    }

    @ApiOperation("根据id查找地址详情")
    @GetMapping("/detail/{address_id}")
    public Result detail(
            @ApiParam(name = "address_id", value = "地址id", required = true) @PathVariable("address_id") Long addressId) {
        return addressService.detail(addressId);
    }

    @ApiOperation("列出所有地址")
    @GetMapping("/list")
    public Result list() {
        return addressService.listAllAddressOfUser();
    }


}

