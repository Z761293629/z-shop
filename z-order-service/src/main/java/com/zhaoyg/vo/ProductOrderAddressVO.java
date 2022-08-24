package com.zhaoyg.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhao
 * @date 2022/8/22
 */
@Data
public class ProductOrderAddressVO {
    @ApiModelProperty("地址id")
    private Long id;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("是否默认收货地址：0->否；1->是")
    @JsonProperty("default_status")
    private Integer defaultStatus;

    @ApiModelProperty("收发货人姓名")
    @JsonProperty("receive_name")
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
    @JsonProperty("detail_address")
    private String detailAddress;
}
