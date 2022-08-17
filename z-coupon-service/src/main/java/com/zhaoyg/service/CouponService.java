package com.zhaoyg.service;

import com.zhaoyg.enums.CouponCategoryEnum;
import com.zhaoyg.model.entity.Coupon;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhaoyg.util.Result;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zhao
 * @since 2022-08-14
 */
public interface CouponService extends IService<Coupon> {

    Result pageCouponActivity(Integer page, Integer size);

    Result getCoupon(Long userId,
                     String userName,
                     Long couponId,
                     CouponCategoryEnum couponCategoryEnum);

    Result newUserCoupon(Long userId, String userName);
}
