package com.zhaoyg.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhao
 * @date 2022/8/16
 */
@Data
public class NewUserCouponRequest {
    @JsonProperty("user_id")
    @NotNull(message = "用户id不能为空")
    private Long userId;

    @JsonProperty("user_name")
    @NotNull(message = "用户昵称不能为空")
    private String userName;
}
