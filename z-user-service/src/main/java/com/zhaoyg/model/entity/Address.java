package com.zhaoyg.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 电商-公司收发货地址表
 * </p>
 *
 * @author zhao
 * @since 2022-08-11
 */
@Data
@ApiModel(value = "Address对象", description = "电商-公司收发货地址表")
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("是否默认收货地址：0->否；1->是")
    private Integer defaultStatus;

    @ApiModelProperty("收发货人姓名")
    private String receiveName;

    @ApiModelProperty("收货人电话")
    private String phone;

    @ApiModelProperty("省/直辖市")
    private String province;

    @ApiModelProperty("市")
    private String city;

    @ApiModelProperty("区")
    private String region;

    @ApiModelProperty("详细地址")
    private String detailAddress;

    private LocalDateTime createTime;


}
