package com.zhaoyg.service;

import com.zhaoyg.entity.ProductOrderMessage;
import com.zhaoyg.model.entity.ProductOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhaoyg.request.ConfirmOrderRequest;
import com.zhaoyg.util.Result;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhao
 * @since 2022-08-20
 */
public interface ProductOrderService extends IService<ProductOrder> {

    Result queryProductOrderState(String orderTradeOutNo);

    Result confirmOrder(ConfirmOrderRequest confirmOrderRequest);

    boolean closeProductOrder(ProductOrderMessage productOrderMessage);

}
