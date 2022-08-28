package com.zhaoyg.controller;

import com.zhaoyg.request.CartItemAddRequest;
import com.zhaoyg.request.CartItemUpdateRequest;
import com.zhaoyg.service.CartService;
import com.zhaoyg.util.Result;
import com.zhaoyg.vo.CartItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhao
 * @date 2022/8/19
 */
@Api(tags = "购物车模块")
@RestController
@RequestMapping("/api/cart/v1")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @ApiOperation("添加购物车")
    @PostMapping("/item")
    public Result addItem(@RequestBody @Validated CartItemAddRequest cartItemAddRequest) {
        return cartService.addItem(cartItemAddRequest);
    }

    @ApiOperation("清空购物车")
    @DeleteMapping
    public Result clearCart() {
        return cartService.clearCart();
    }

    @ApiOperation("查看我的购物车")
    @GetMapping
    public Result myCart(
            @ApiParam(name = "latest_value", value = "是否取最新价格") @RequestParam(value = "latest_amount", required = false) Boolean latestAmount) {
        return cartService.myCart(latestAmount);
    }

    @ApiOperation("删除购物项")
    @DeleteMapping("/item/{product_id:\\d+}")
    public Result removeItem(
            @ApiParam(name = "product_id", value = "商品id") @PathVariable("product_id") Long productId) {
        return cartService.removeItem(productId);
    }

    @ApiOperation("更新购物车数量")
    @PutMapping("/item")
    public Result updateItem(@RequestBody @Validated CartItemUpdateRequest cartItemUpdateRequest) {
        return cartService.updateItem(cartItemUpdateRequest);
    }

    @PostMapping("/confirm_order_cart_items")
    public Result confirmOrderCartItems(@RequestBody List<Long> productIds, @RequestParam("order_out_trade_no") String orderOutTradeNo) {
        List<CartItemVO> items = cartService.confirmOrderCartItems(productIds, orderOutTradeNo);
        return Result.success(items);
    }


}
