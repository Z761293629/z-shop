package com.zhaoyg.controller;


import com.zhaoyg.model.entity.Address;
import com.zhaoyg.service.AddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/api/v1/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @ApiOperation("根据id查找地址详情")
    @GetMapping("/detail/{address_id}")
    public Address detail(
            @ApiParam(name = "address_id", value = "地址id", required = true) @PathVariable("address_id") Long addressId) {
        return addressService.getById(addressId);
    }


}

