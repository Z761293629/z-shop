package com.zhaoyg.mapper;

import com.zhaoyg.model.entity.CouponRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zhao
 * @since 2022-08-14
 */
public interface CouponRecordMapper extends BaseMapper<CouponRecord> {

    Integer lockUseStateBatch(@Param("userId") Long id,
                              @Param("useState") String useState,
                              @Param("couponRecordIds") List<Long> couponRecordIds);

    void updateUseState(@Param("couponRecordId") Long couponRecordId, @Param("useState") String useState);
}
