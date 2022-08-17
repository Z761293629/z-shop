package com.zhaoyg.controller;


import com.zhaoyg.entity.LoginUser;
import com.zhaoyg.enums.BizCodeEnum;
import com.zhaoyg.enums.CouponCategoryEnum;
import com.zhaoyg.interceptor.AuthInterceptor;
import com.zhaoyg.request.NewUserCouponRequest;
import com.zhaoyg.request.PromotionCouponRequest;
import com.zhaoyg.service.CouponService;
import com.zhaoyg.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zhao
 * @since 2022-08-14
 */
@Api(tags = "优惠券模块")
@Slf4j
@RestController
@RequestMapping("/api/coupon/v1")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;
    private final RedissonClient redissonClient;

    @ApiOperation("分页查询优惠券")
    @GetMapping("/page")
    public Result pageCouponActivity(
            @ApiParam(name = "page", value = "当前页码") @RequestParam(name = "page", defaultValue = "1") Integer page,
            @ApiParam(name = "size", value = "每页条数") @RequestParam(name = "size", defaultValue = "20") Integer size) {
        return couponService.pageCouponActivity(page, size);
    }

    @ApiOperation("领取优惠券")
    @PostMapping("/promotion")
    public Result getPromotionCoupon(
            @RequestBody PromotionCouponRequest promotionCouponRequest) {
        // 分布式锁 解决超领
        // 放在事务外面，防止事务尚未提交 锁已被释放，被其他获取 看不到未提交的事务
        /*
            1. REDA_UNCOMMITTED
            2. READ_COMMITTED      MVVC
            3. REPEAT_READ         MVVC  (MySQL 默认使用)
            4. SERIALIZE
         */
        LoginUser loginUser = AuthInterceptor.loginUserThreadLocal.get();
        // String key = "lock:coupon:" + loginUser.getId();
        // 减小锁粒度 lock:coupon:{couponId}:{userId}
        String key = "lock:coupon:" + promotionCouponRequest.getCouponId() + ":" + loginUser.getId();
        RLock rLock = redissonClient.getLock(key);
        try {
            // 设置了leaseTime 到期自动释放 不存在watchDog
            //boolean lock = rLock.tryLock(5, 30, TimeUnit.SECONDS);
            // 没有设置leaseTime watchDog自动续期
            boolean lock = rLock.tryLock(5, TimeUnit.SECONDS);
            if (lock) {
                // 观察watchDog自动续期 10s -> ttl 30s
                //TimeUnit.MINUTES.sleep(10);
                return couponService.getCoupon(loginUser.getId(), loginUser.getName(), promotionCouponRequest.getCouponId(), CouponCategoryEnum.PROMOTION);
            }
        } catch (InterruptedException e) {
            log.error("[领取优惠券] 异常", e);
            Thread.currentThread().interrupt();
        } finally {
            rLock.unlock();
        }
        return Result.fail(BizCodeEnum.COUPON_LOCK_FAIL);
    }

    @ApiOperation("新人注册优惠券")
    @PostMapping("new_user")
    public Result getNewUserCoupon(@RequestBody @Validated NewUserCouponRequest newUserCouponRequest) {
        String key = "lock:coupon:new:" + newUserCouponRequest.getUserId();
        RLock rLock = redissonClient.getLock(key);
        try {
            boolean lock = rLock.tryLock(3, TimeUnit.SECONDS);
            if (lock) {
                return couponService.newUserCoupon(newUserCouponRequest.getUserId(), newUserCouponRequest.getUserName());
            }
        } catch (InterruptedException e) {
            log.error("[领取新人优惠券] 异常", e);
            Thread.currentThread().interrupt();
        } finally {
            rLock.unlock();
        }
        return Result.fail(BizCodeEnum.COUPON_LOCK_FAIL);
    }

}

