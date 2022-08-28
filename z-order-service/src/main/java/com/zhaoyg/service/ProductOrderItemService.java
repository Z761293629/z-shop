package com.zhaoyg.service;

import com.zhaoyg.model.entity.ProductOrderItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhao
 * @since 2022-08-20
 */
public interface ProductOrderItemService extends IService<ProductOrderItem> {

    List<ProductOrderItem> listByOrderIds(List<Long> orderIds);

}
