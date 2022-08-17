package com.zhaoyg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhaoyg.enums.BizCodeEnum;
import com.zhaoyg.enums.CouponCategoryEnum;
import com.zhaoyg.enums.CouponPublishEnum;
import com.zhaoyg.enums.CouponUseStateEnum;
import com.zhaoyg.exception.BizException;
import com.zhaoyg.model.entity.Coupon;
import com.zhaoyg.mapper.CouponMapper;
import com.zhaoyg.model.entity.CouponRecord;
import com.zhaoyg.service.CouponRecordService;
import com.zhaoyg.service.CouponService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhaoyg.util.Result;
import com.zhaoyg.vo.CouponVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhao
 * @since 2022-08-14
 */
@Service
@RequiredArgsConstructor
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {
    private final CouponRecordService couponRecordService;

    @Override
    public Result pageCouponActivity(Integer page, Integer size) {

        LambdaQueryWrapper<Coupon> queryWrapper = Wrappers.<Coupon>lambdaQuery()
                .eq(Coupon::getCategory, CouponCategoryEnum.PROMOTION)
                .eq(Coupon::getPublish, CouponPublishEnum.PUBLISH)
                .orderByDesc(Coupon::getCreateTime);
        Page<Coupon> couponPage = page(Page.of(page, size),
                queryWrapper);
        Map<String, Object> pageMap = new HashMap<>(4);
        pageMap.put("total", couponPage.getTotal());
        pageMap.put("current", couponPage.getCurrent());
        List<CouponVO> couponVOList = Optional.ofNullable(couponPage.getRecords())
                .stream()
                .flatMap(Collection::stream)
                .map(this::entityToVo)
                .collect(Collectors.toList());
        pageMap.put("records", couponVOList);
        return Result.success(pageMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result getCoupon(
            Long userId,
            String userName,
            Long couponId,
            CouponCategoryEnum couponCategoryEnum) {
        Coupon coupon = getOne(
                Wrappers.<Coupon>lambdaQuery()
                        .eq(Coupon::getId, couponId)
                        .eq(Coupon::getCategory, couponCategoryEnum)
        );
        checkCoupon(coupon, userId);
        // 尝试减少库存
        // 数据库判断解决超卖
        int row = reduceCouponStock(coupon);
        if (row == 1) {
            CouponRecord couponRecord = CouponRecord.builder()
                    .couponId(couponId)
                    .couponTitle(coupon.getCouponTitle())
                    .conditionPrice(coupon.getConditionPrice())
                    .price(coupon.getPrice())
                    .startTime(coupon.getStartTime())
                    .endTime(coupon.getEndTime())
                    .userId(userId)
                    .userName(userName)
                    .useState(CouponUseStateEnum.NEW.name())
                    .createTime(LocalDateTime.now())
                    .build();
            couponRecordService.save(couponRecord);
            // 模拟异常，查看事务是否生效
            //int i = 1 / 0;
            return Result.success();
        }
        return Result.fail(BizCodeEnum.COUPON_UNDER_STOCK);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result newUserCoupon(Long userId, String userName) {
        // 新人优惠券数量不多 可以遍历发放
        List<Coupon> couponList = list(
                Wrappers.<Coupon>lambdaQuery()
                        .eq(Coupon::getPublish, CouponPublishEnum.PUBLISH)
                        .eq(Coupon::getCategory, CouponCategoryEnum.NEW_USER)
        );
        for (Coupon coupon : couponList) {
            this.getCoupon(userId, userName, coupon.getId(), CouponCategoryEnum.NEW_USER);
        }
        return Result.success();
    }

    private int reduceCouponStock(Coupon coupon) {
        /*
        version 1 : UPDATE z_coupon.coupon SET stock = stock - 1 WHERE id = #{id} 会出现超卖问题
        version 2 : UPDATE z_coupon.coupon SET stock = stock - 1 WHERE id = #{id} AND stock > 0
         */
        return baseMapper.reduceCouponStock(coupon.getId());
    }

    private void checkCoupon(Coupon coupon, Long userId) {
        // 优惠券不存在
        if (Objects.isNull(coupon)) {
            throw new BizException(BizCodeEnum.COUPON_DONT_EXIST);
        }
        // 优惠券尚未发布
        if (!Objects.equals(coupon.getPublish(), CouponPublishEnum.PUBLISH.name())) {
            throw new BizException(BizCodeEnum.COUPON_UNPUBLISHED);
        }
        // 库存不足
        if (coupon.getStock() <= 0) {
            throw new BizException(BizCodeEnum.COUPON_UNDER_STOCK);
        }
        // 未到开始时间
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime())) {
            throw new BizException(BizCodeEnum.COUPON_BEFORE_TIME);
        }
        // 超过结束时间
        if (now.isAfter(coupon.getEndTime())) {
            throw new BizException(BizCodeEnum.COUPON_AFTER_TIME);
        }

        // 超过额度限制
        long count = couponRecordService.count(
                Wrappers.<CouponRecord>lambdaQuery()
                        .eq(CouponRecord::getCouponId, coupon.getId())
                        .eq(CouponRecord::getUserId, userId)
        );
        if (count >= coupon.getUserLimit()) {
            throw new BizException(BizCodeEnum.COUPON_SIZE_LIMIT);
        }
    }

    private CouponVO entityToVo(Coupon coupon) {
        CouponVO couponVO = new CouponVO();
        BeanUtils.copyProperties(coupon, couponVO);
        return couponVO;
    }
}
