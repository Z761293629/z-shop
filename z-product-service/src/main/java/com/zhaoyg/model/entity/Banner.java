package com.zhaoyg.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
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
 * @since 2022-08-17
 */
@Getter
@Setter
@ApiModel(value = "Banner对象")
public class Banner implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("图片")
    private String img;

    @ApiModelProperty("跳转地址")
    private String url;

    @ApiModelProperty("权重")
    private Integer weight;


}
