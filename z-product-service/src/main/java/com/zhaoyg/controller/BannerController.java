package com.zhaoyg.controller;


import com.zhaoyg.service.BannerService;
import com.zhaoyg.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zhao
 * @since 2022-08-17
 */
@Api(tags = "banner")
@RestController
@RequestMapping("/api/banner/v1")
@RequiredArgsConstructor
public class BannerController {
    private final BannerService bannerService;

    @ApiOperation("banner列表")
    @GetMapping
    public Result list() {
        return bannerService.listBanner();
    }


}

