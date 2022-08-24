package com.zhaoyg.controller;


import com.zhaoyg.request.ConfirmOrderRequest;
import com.zhaoyg.service.ProductOrderService;
import com.zhaoyg.util.Result;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zhao
 * @since 2022-08-20
 */
@Validated
@RestController
@RequestMapping("/api/order/v1")
@RequiredArgsConstructor
public class ProductOrderController {
    private final ProductOrderService productOrderService;


    @ApiOperation("订单状态")
    @GetMapping("/state")
    public Result queryProductOrderState(@NotNull(message = "订单号不能为空") @RequestParam("order_trade_out_no") String orderTradeOutNo) {
        return productOrderService.queryProductOrderState(orderTradeOutNo);
    }

    @PostMapping("/confirm")
    public void confirmOrder(@RequestBody ConfirmOrderRequest confirmOrderRequest, HttpServletResponse response) {
        Result result = productOrderService.confirmOrder(confirmOrderRequest);
    }


}

