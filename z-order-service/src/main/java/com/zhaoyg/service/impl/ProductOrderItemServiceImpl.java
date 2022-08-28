package com.zhaoyg.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhaoyg.model.entity.ProductOrderItem;
import com.zhaoyg.mapper.ProductOrderItemMapper;
import com.zhaoyg.service.ProductOrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhao
 * @since 2022-08-20
 */
@Service
public class ProductOrderItemServiceImpl extends ServiceImpl<ProductOrderItemMapper, ProductOrderItem> implements ProductOrderItemService {

    @Override
    public List<ProductOrderItem> listByOrderIds(List<Long> orderIds) {
        return list(Wrappers.<ProductOrderItem>lambdaQuery().in(ProductOrderItem::getProductOrderId, orderIds));
    }
}
