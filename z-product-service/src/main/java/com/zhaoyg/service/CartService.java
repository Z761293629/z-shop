package com.zhaoyg.service;

import com.zhaoyg.entity.CartItemMessage;
import com.zhaoyg.request.CartItemAddRequest;
import com.zhaoyg.request.CartItemUpdateRequest;
import com.zhaoyg.util.Result;
import com.zhaoyg.vo.CartItemVO;

import java.util.List;

/**
 * @author zhao
 * @date 2022/8/19
 */
public interface CartService {
    Result addItem(CartItemAddRequest cartItemAddRequest);

    Result clearCart();

    Result myCart(Boolean latestAmount);

    Result removeItem(Long productId);

    Result updateItem(CartItemUpdateRequest cartItemUpdateRequest);

    List<CartItemVO> confirmOrderCartItems(List<Long> productIds, String orderTradeOutNo);

    void recoverItem(CartItemMessage productMessage);
}
