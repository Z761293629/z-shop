package com.zhaoyg.controller;


import com.zhaoyg.service.CouponRecordService;
import com.zhaoyg.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zhao
 * @since 2022-08-14
 */
@Api(tags = "优惠券记录模块")
@RestController
@RequestMapping("/api/coupon_record/v1")
@RequiredArgsConstructor
public class CouponRecordController {
    private final CouponRecordService couponRecordService;

    @ApiOperation("分页查询")
    @GetMapping("/page")
    public Result page(
            @ApiParam(name = "page", value = "页码") @RequestParam(name = "page", defaultValue = "1") Integer page,
            @ApiParam(name = "size", value = "每页条数") @RequestParam(name = "size", defaultValue = "20") Integer size) {

        return couponRecordService.page(page, size);
    }

    @ApiOperation("优惠券记录详情")
    @GetMapping("{record_id}")
    public Result detail(
            @ApiParam(name = "record_id", value = "优惠券记录id") @PathVariable(name = "record_id") Long recordId) {
        return couponRecordService.detail(recordId);
    }


}

