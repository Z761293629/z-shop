package com.zhaoyg.enums;

import lombok.Getter;

/**
 * 统一状态码及错误信息
 *
 * @author zhao
 * @date 2022/8/11
 */
public enum BizCodeEnum {
    /**
     * 通用操作码
     */
    OPS_REPEAT(110001, "重复操作"),
    PARAM_ILLEGAL(110002, "参数异常"),

    /**
     * 验证码
     */
    CODE_TO_ERROR(240001, "接收号码不合规"),
    CODE_LIMITED(240002, "验证码发送过快"),
    CODE_ERROR(240003, "验证码错误"),
    CODE_CAPTCHA(240101, "图形验证码错误"),

    /**
     * 账号
     */
    ACCOUNT_REPEAT(250001, "账号已经存在"),
    ACCOUNT_UNREGISTER(250002, "账号不存在"),
    ACCOUNT_PWD_ERROR(250003, "账号或者密码错误"),

    ACCOUNT_UNAUTHORIZED(250403, "账号未登录"),
    HEAD_UPLOAD_ERROR(250004, "上传头像错误"),
    ADDRESS_DONT_EXIST(270001, "地址不存在"),

    ADDRESS_DELETE_ERROR(270002, "删除地址失败"),


    COUPON_DONT_EXIST(310001, "优惠券不存在"),
    COUPON_UNPUBLISHED(310002, "优惠券不存在"),
    COUPON_UNDER_STOCK(310003, "库存不足"),
    COUPON_BEFORE_TIME(310004, "领取时间未到"),
    COUPON_AFTER_TIME(310005, "领取时间已过"),
    COUPON_SIZE_LIMIT(310006, "超过领取数量"),
    COUPON_GET_FAIL(310007, "领取失败"),
    COUPON_LOCK_FAIL(310008, "请勿重复调用"),

    COUPON_RECORD_DONT_EXIST(320001, "优惠券记录不存在"),
    COUPON_RECORD_LOCK_FAIL(320002, "优惠券记录锁定失败"),


    PRODUCT_NOT_EXIST(410001, "商品不存在"),
    PRODUCT_STOCK_LOCK_FAIL(410001, "商品库存锁定失败"),

    CART_EMPTY(420001, "购物车为空"),

    CART_PRODUCT_DONT_EXIT(420002, "购物车无该产品"),


    ORDER_DONT_EXIST(510001, "订单不存在"),
    ORDER_CONFIRM_CART_ITEM_DONT_EXIST(510002, "购物车商品项不存在"),
    ORDER_CONFIRM_COUPON_FAIL(510003, "优惠券验券失败"),
    ORDER_ALIPAY_FAIL(510004, "ali支付失败"),
    ORDER_STATE_ILLEGAL(510005, "订单状态非法"),
    ORDER_CONFIRM_TOKEN_EQUAL_FAIL(510006, "订单令牌不正确"),
    ;


    @Getter
    private final int code;
    @Getter
    private final String message;

    BizCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
