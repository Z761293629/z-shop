package com.zhaoyg.mapper;

import com.zhaoyg.model.entity.Coupon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhao
 * @since 2022-08-14
 */
public interface CouponMapper extends BaseMapper<Coupon> {

    int reduceCouponStock(@Param("id") Long id);
}
