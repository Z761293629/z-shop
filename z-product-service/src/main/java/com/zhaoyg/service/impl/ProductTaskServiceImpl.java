package com.zhaoyg.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhaoyg.enums.StockTaskStateEnum;
import com.zhaoyg.model.entity.ProductTask;
import com.zhaoyg.mapper.ProductTaskMapper;
import com.zhaoyg.service.ProductTaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhao
 * @since 2022-08-21
 */
@Service
public class ProductTaskServiceImpl extends ServiceImpl<ProductTaskMapper, ProductTask> implements ProductTaskService {

    @Override
    public void updateLockState(Long productTaskId, StockTaskStateEnum stockTaskStateEnum) {
        update(
                Wrappers.<ProductTask>lambdaUpdate()
                        .eq(ProductTask::getId, productTaskId)
                        .set(ProductTask::getLockState, stockTaskStateEnum.name())

        );
    }
}
