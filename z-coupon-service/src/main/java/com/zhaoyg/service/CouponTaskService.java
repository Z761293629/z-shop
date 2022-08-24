package com.zhaoyg.service;

import com.zhaoyg.enums.StockTaskStateEnum;
import com.zhaoyg.model.entity.CouponTask;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhao
 * @since 2022-08-21
 */
public interface CouponTaskService extends IService<CouponTask> {

    Integer insertBatch(List<CouponTask> couponTaskList);

    void updateLockState(Long couponTaskId, StockTaskStateEnum lockState);
}
