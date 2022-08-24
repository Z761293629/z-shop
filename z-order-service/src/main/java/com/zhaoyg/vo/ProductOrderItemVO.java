package com.zhaoyg.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * @author zhao
 * @date 2022/8/22
 */
public class ProductOrderItemVO {
    @JsonProperty("product_id")
    private Long produceId;
    @JsonProperty("product_title")
    private String productTitle;
    @JsonProperty("product_img")
    private String productImg;
    private BigDecimal amount;
    @JsonProperty("buy_num")
    private Integer buyNum;
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    public Long getProduceId() {
        return produceId;
    }

    public void setProduceId(Long produceId) {
        this.produceId = produceId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(Integer buyNum) {
        this.buyNum = buyNum;
    }

    public BigDecimal getTotalAmount() {
        return amount.multiply(BigDecimal.valueOf(this.buyNum));
    }
}
