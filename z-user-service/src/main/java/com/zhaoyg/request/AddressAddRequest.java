package com.zhaoyg.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author zhao
 * @date 2022/8/14
 */
@ApiModel(value = "新增地址请求")
@Data
public class AddressAddRequest {


    @ApiModelProperty(value = "收发货人姓名", example = "二仙桥大爷")
    @JsonProperty("receive_name")
    private String receiveName;

    @ApiModelProperty(value = "收货人电话", example = "13866668888")
    private String phone;

    @ApiModelProperty(value = "省/直辖市", example = "江苏")
    private String province;

    @ApiModelProperty(value = "市", example = "南京")
    private String city;

    @ApiModelProperty(value = "区", example = "江宁")
    private String region;

    @ApiModelProperty(value = "详细地址", example = "成华大道")
    @JsonProperty("detail_address")
    private String detailAddress;

    @ApiModelProperty("是否默认收货地址：0->否；1->是")
    @JsonProperty("default_status")
    private Integer defaultStatus;

}
