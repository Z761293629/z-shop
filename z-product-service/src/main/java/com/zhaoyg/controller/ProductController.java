package com.zhaoyg.controller;


import com.zhaoyg.request.LockProductRequest;
import com.zhaoyg.service.ProductService;
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
 * @since 2022-08-17
 */
@Api(tags = "商品模块")
@RestController
@RequestMapping("/api/product/v1")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;


    @ApiOperation("分页查询商品")
    @GetMapping("/page")
    public Result page(
            @ApiParam(name = "page", value = "当前页码") @RequestParam(value = "page", defaultValue = "1") Integer page,
            @ApiParam(name = "size", value = "每页条数") @RequestParam(value = "size", defaultValue = "10") Integer size) {

        return productService.page(page, size);
    }

    @ApiOperation("商品详情")
    @GetMapping("{id}")
    public Result detail(@PathVariable("id") Long id) {
        return productService.detail(id);
    }

    @ApiOperation("锁定商品")
    @PostMapping("/lock_products")
    public Result lockProducts(@RequestBody LockProductRequest lockProductRequest) {
        return productService.lockProducts(lockProductRequest);
    }
}

