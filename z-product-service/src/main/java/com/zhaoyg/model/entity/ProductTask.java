package com.zhaoyg.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * <p>
 *
 * </p>
 *
 * @author zhao
 * @since 2022-08-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("product_task")
@ApiModel(value = "ProductTask对象", description = "")
public class ProductTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("商品id")
    private Long productId;

    @ApiModelProperty("购买数量")
    private Integer buyNum;

    @ApiModelProperty("商品标题")
    private String productName;

    @ApiModelProperty("锁定状态锁定LOCK  完成FINISH-取消CANCEL")
    private String lockState;

    private String outTradeNo;

    private LocalDateTime createTime;


}
