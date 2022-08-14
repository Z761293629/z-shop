package com.zhaoyg.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zhao
 * @date 2022/8/13
 */
@Data
public class UserVO {

    private Long id;

    @ApiModelProperty("昵称")
    private String name;


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
}
