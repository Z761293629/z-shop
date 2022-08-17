package com.zhaoyg.enums;

/**
 * @author zhao
 * @date 2022/8/15
 */
public enum CouponUseStateEnum {
    NEW("可用"),
    USED("已使用"),
    EXPIRED("过期");

    private final String description;


    CouponUseStateEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
