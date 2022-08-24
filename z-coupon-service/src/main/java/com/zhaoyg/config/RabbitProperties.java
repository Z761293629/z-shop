package com.zhaoyg.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhao
 * @date 2022/8/21
 */
@Data
@ConfigurationProperties("rabbit")
public class RabbitProperties {

    /**
     * 交换机
     */
    private String couponEventExchange;


    /**
     * 第一个队列延迟队列，
     */
    private String couponReleaseDelayQueue;

    /**
     * 第一个队列的路由key
     * 进入队列的路由key
     */
    private String couponReleaseDelayRoutingKey;


    /**
     * 第二个队列，被监听恢复库存的队列
     */
    private String couponReleaseQueue;

    /**
     * 第二个队列的路由key
     * 即进入死信队列的路由key
     */
    private String couponReleaseRoutingKey;

    /**
     * 过期时间
     */
    private Integer ttl;


}
