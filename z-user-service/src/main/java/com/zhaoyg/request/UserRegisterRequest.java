package com.zhaoyg.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author zhao
 * @date 2022/8/13
 */
@ApiModel("用户注册请求")
@Data
public class UserRegisterRequest {

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称", example = "zhao")
    private String name;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码", example = "123456")
    private String pwd;


    /**
     * 头像
     */
    @ApiModelProperty(value = "头像", example = "http://127.0.0.1:9000/z-shop-img/user/2022/08/13/k686lk90.png")
    private String headImg;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱", example = "zhaoyg@wisevirtue.com")
    private String mail;

    /**
     * 个签
     */
    @ApiModelProperty(value = "个签", example = "好好学习，天天向上")
    private String slogan;

    /**
     * 性别 1=男 0=女
     */
    @ApiModelProperty(value = "性别 1=男 0=女", example = "1")
    private Integer sex;

    /**
     * 验证码
     */
    @ApiModelProperty(value = "验证码", example = "1234")
    private String code;
}
