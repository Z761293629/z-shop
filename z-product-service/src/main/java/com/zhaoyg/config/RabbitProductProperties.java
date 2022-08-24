package com.zhaoyg.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhao
 * @date 2022/8/21
 */
@Data
@ConfigurationProperties("rabbit.product")
public class RabbitProductProperties {

    /**
     * 交换机
     */
    private String eventExchange;


    /**
     * 第一个队列延迟队列，
     */
    private String releaseDelayQueue;

    /**
     * 第一个队列的路由key
     * 进入队列的路由key
     */
    private String releaseDelayRoutingKey;


    /**
     * 第二个队列，被监听恢复库存的队列
     */
    private String releaseQueue;

    /**
     * 第二个队列的路由key
     * 即进入死信队列的路由key
     */
    private String releaseRoutingKey;

    /**
     * 过期时间
     */
    private Integer ttl;


}
