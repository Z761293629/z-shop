package com.zhaoyg.enums;

/**
 * @author zhao
 * @date 2022/8/14
 */
public enum AddressStatusEnum {
    DEFAULT_STATUS(1),
    COMMON_STATUS(0);

    private final int status;

    AddressStatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
