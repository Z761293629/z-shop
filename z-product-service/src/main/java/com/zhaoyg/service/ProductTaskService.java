package com.zhaoyg.service;

import com.zhaoyg.enums.StockTaskStateEnum;
import com.zhaoyg.model.entity.ProductTask;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zhao
 * @since 2022-08-21
 */
public interface ProductTaskService extends IService<ProductTask> {

    void updateLockState(Long productTaskId, StockTaskStateEnum stockTaskStateEnum);
}
