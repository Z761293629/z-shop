package com.zhaoyg.service;

import com.zhaoyg.entity.ProductOrderMessage;
import com.zhaoyg.enums.ProductOrderPayTypeEnum;
import com.zhaoyg.model.entity.ProductOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhaoyg.request.ConfirmOrderRequest;
import com.zhaoyg.request.RepayOrderRequest;
import com.zhaoyg.util.Result;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhao
 * @since 2022-08-20
 */
public interface ProductOrderService extends IService<ProductOrder> {

    Result queryProductOrderState(String orderOutTradeNo);

    Result confirmOrder(ConfirmOrderRequest confirmOrderRequest);

    boolean closeProductOrder(ProductOrderMessage productOrderMessage);

    void handlePayCallback(ProductOrderPayTypeEnum payTypeEnum, Map<String, String> map);

    Result pageOrder(Integer page, Integer size, String state);

    Result repayOrder(RepayOrderRequest repayOrderRequest);
}
