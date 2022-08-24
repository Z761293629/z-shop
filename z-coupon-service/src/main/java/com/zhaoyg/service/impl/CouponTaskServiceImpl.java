package com.zhaoyg.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhaoyg.enums.StockTaskStateEnum;
import com.zhaoyg.model.entity.CouponTask;
import com.zhaoyg.mapper.CouponTaskMapper;
import com.zhaoyg.service.CouponTaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhao
 * @since 2022-08-21
 */
@Service
public class CouponTaskServiceImpl extends ServiceImpl<CouponTaskMapper, CouponTask> implements CouponTaskService {

    @Override
    public Integer insertBatch(List<CouponTask> couponTaskList) {
        return baseMapper.insertBatch(couponTaskList);
    }

    @Override
    public void updateLockState(Long couponTaskId, StockTaskStateEnum lockState) {
        update(
                Wrappers.<CouponTask>lambdaUpdate()
                        .eq(CouponTask::getId, couponTaskId)
                        .set(CouponTask::getLockState, lockState.name())
        );
    }

}
