package com.zhaoyg.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhao
 * @date 2022/8/17
 */
@Data
@ApiModel("banner")
public class BannerVO {
    private Integer id;

    @ApiModelProperty("图片")
    private String img;

    @ApiModelProperty("跳转地址")
    private String url;

    @ApiModelProperty("权重")
    private Integer weight;
}
