package com.zhaoyg.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author zhao
 * @date 2022/8/13
 */
@Data
public class LoginUser {

    private Long id;
    private String name;
    private String mail;
    @JsonProperty("head_img")
    private String headImg;

    private String ip;

}
