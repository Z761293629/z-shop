package com.zhaoyg.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhao
 * @date 2022/8/13
 */
@ApiModel("用户登录请求")
@Data
public class UserLoginRequest {

    @ApiModelProperty(value = "邮箱", example = "zhaoyg@wisevirtue.com")
    private String mail;

    @ApiModelProperty(value = "密码", example = "123456")
    private String pwd;

}
