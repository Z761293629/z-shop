package com.zhaoyg.mapper;

import com.zhaoyg.model.entity.CouponTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhao
 * @since 2022-08-21
 */
public interface CouponTaskMapper extends BaseMapper<CouponTask> {

    Integer insertBatch(@Param("couponTaskList") List<CouponTask> couponTaskList);
}
