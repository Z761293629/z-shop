package com.zhaoyg.enums;

/**
 * @author zhao
 * @date 2022/8/14
 */
public enum CouponPublishEnum {

    PUBLISH("发布"),
    DRAFT("草稿"),
    OFFLINE("下线");
    private final String description;

    CouponPublishEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
