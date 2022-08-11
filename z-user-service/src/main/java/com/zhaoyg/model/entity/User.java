package com.zhaoyg.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author zhao
 * @since 2022-08-11
 */
@Getter
@Setter
@ApiModel(value = "User对象", description = "")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("昵称")
    private String name;

    @ApiModelProperty("密码")
    private String pwd;

    @ApiModelProperty("头像")
    private String headImg;

    @ApiModelProperty("用户签名")
    private String slogan;

    @ApiModelProperty("0表示女，1表示男")
    private Integer sex;

    @ApiModelProperty("积分")
    private Integer points;

    private LocalDateTime createTime;

    @ApiModelProperty("邮箱")
    private String mail;

    @ApiModelProperty("盐，用于个人敏感信息处理")
    private String secret;


}
