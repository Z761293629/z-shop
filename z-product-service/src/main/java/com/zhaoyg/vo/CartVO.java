package com.zhaoyg.vo;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhao
 * @date 2022/8/19
 */
@ApiModel("购物车")
public class CartVO {

    @JsonProperty("cart_items")
    private List<CartItemVO> cartItems;

    @JsonProperty("total_num")
    private Integer totalNum;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    @JsonProperty("real_pay_amount")
    private BigDecimal realPayAmount;

    public BigDecimal getTotalAmount() {
        return CollUtil.isEmpty(cartItems) ?
                BigDecimal.ZERO : cartItems.stream().map(CartItemVO::getTotalAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public Integer getTotalNum() {
        return CollUtil.isEmpty(cartItems) ?
                0 : cartItems.stream().mapToInt(CartItemVO::getBuyNum).sum();
    }

    public List<CartItemVO> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItemVO> cartItems) {
        this.cartItems = cartItems;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }
}
