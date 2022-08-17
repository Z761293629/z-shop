package com.zhaoyg.enums;

/**
 * @author zhao
 * @date 2022/8/14
 */
public enum CouponCategoryEnum {
    NEW_USER("注册赠券"),
    TASK("任务卷"),
    PROMOTION("促销劵");

    private final String description;

    CouponCategoryEnum(String description) {
        this.description = description;
    }


    public String getDescription() {
        return description;
    }
}
