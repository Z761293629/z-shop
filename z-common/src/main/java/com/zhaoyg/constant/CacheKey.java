package com.zhaoyg.constant;

/**
 * @author zhao
 * @date 2022/8/12
 */
public interface CacheKey {

    String CODE_KEY = "code:%s:%s";
    String CART_KEY = "cart:%s";

    String ORDER_TOKEN_KEN = "order:confirm:%s";

    static String codeKey(String biz, String identify) {
        return String.format(CODE_KEY, biz, identify);
    }
}
