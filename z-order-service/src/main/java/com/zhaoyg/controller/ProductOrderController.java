package com.zhaoyg.controller;


import cn.hutool.core.util.RandomUtil;
import com.zhaoyg.constant.CacheKey;
import com.zhaoyg.entity.LoginUser;
import com.zhaoyg.interceptor.AuthInterceptor;
import com.zhaoyg.request.ConfirmOrderRequest;
import com.zhaoyg.request.RepayOrderRequest;
import com.zhaoyg.service.ProductOrderService;
import com.zhaoyg.util.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zhao
 * @since 2022-08-20
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/order/v1")
@RequiredArgsConstructor
public class ProductOrderController {
    private final ProductOrderService productOrderService;
    private final RedisTemplate<String, String> redisTemplate;


    @ApiOperation("订单状态")
    @GetMapping("/state")
    public Result queryProductOrderState(
            @NotNull(message = "订单号不能为空") @RequestParam("order_out_trade_no") String orderOutTradeNo) {
        return productOrderService.queryProductOrderState(orderOutTradeNo);
    }

    @ApiOperation("分页查询优惠券")
    @GetMapping("/page")
    public Result pageOrder(
            @ApiParam(name = "page", value = "当前页码") @RequestParam(name = "page", defaultValue = "1") Integer page,
            @ApiParam(name = "size", value = "每页条数") @RequestParam(name = "size", defaultValue = "20") Integer size,
            @ApiParam(name = "state", value = "订单状态") @RequestParam(name = "state", defaultValue = "") String state) {
        return productOrderService.pageOrder(page, size, state);
    }


    @PostMapping("/confirm")
    public void confirmOrder(@RequestBody @Validated ConfirmOrderRequest confirmOrderRequest, HttpServletResponse response) {
        Result result = productOrderService.confirmOrder(confirmOrderRequest);
        writeData(response, result);
    }

    private static void writeData(HttpServletResponse response, Result result) {
        if (result.ok()) {
            response.setContentType("text/html;charset=UTF8");
            try (PrintWriter writer = response.getWriter()) {
                writer.write(result.getData().toString());
                writer.flush();
            } catch (IOException exception) {
                log.error("[调用支付] 异常", exception);
            }
        }
    }

    @PostMapping("/repay")
    public void repayOrder(@RequestBody RepayOrderRequest repayOrderRequest, HttpServletResponse response) {
        Result result = productOrderService.repayOrder(repayOrderRequest);
        writeData(response, result);
    }

    @GetMapping("/order_token")
    public Result orderToken() {
        LoginUser loginUser = AuthInterceptor.loginUserThreadLocal.get();
        String key = String.format(CacheKey.ORDER_TOKEN_KEN, loginUser.getId());
        String value = RandomUtil.randomString(16);
        redisTemplate.opsForValue().set(key, value, 30, TimeUnit.MINUTES);
        return Result.success(value);
    }


}

