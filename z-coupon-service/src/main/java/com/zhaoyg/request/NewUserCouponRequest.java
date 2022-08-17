package com.zhaoyg.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhao
 * @date 2022/8/16
 */
@Data
@ApiModel("新用户优惠券请求")
public class NewUserCouponRequest {
    @ApiModelProperty("用户id")
    @JsonProperty("user_id")
    @NotNull(message = "用户id不能为空")
    private Long userId;

    @ApiModelProperty("用户昵称")
    @JsonProperty("user_name")
    @NotNull(message = "用户昵称不能为空")
    private String userName;
}
