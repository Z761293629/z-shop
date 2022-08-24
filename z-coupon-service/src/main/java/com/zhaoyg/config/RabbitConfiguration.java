package com.zhaoyg.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhao
 * @date 2022/8/21
 */
@Configuration
@EnableConfigurationProperties(RabbitProperties.class)
public class RabbitConfiguration {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * topic 交换器
     *
     * @param rabbitProperties rabbit 配置参数
     * @return 交换器
     */
    @Bean
    public Exchange exchange(RabbitProperties rabbitProperties) {
        return new TopicExchange(rabbitProperties.getCouponEventExchange(), true, false);
    }

    /**
     * 延迟队列
     *
     * @param rabbitProperties rabbit 配置参数
     * @return 延迟队列
     */
    @Bean
    public Queue couponReleaseDelayQueue(RabbitProperties rabbitProperties) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("x-message-ttl", rabbitProperties.getTtl());
        paramMap.put("x-dead-letter-exchange", rabbitProperties.getCouponEventExchange());
        paramMap.put("x-dead-letter-routing-key", rabbitProperties.getCouponReleaseRoutingKey());
        return new Queue(
                rabbitProperties.getCouponReleaseDelayQueue(),
                true,
                false,
                false,
                paramMap
        );
    }

    /**
     * 绑定 延时队列和交换机
     *
     * @param rabbitProperties rabbit 配置参数
     * @return Binding
     */
    @Bean
    public Binding couponReleaseDelayBinding(RabbitProperties rabbitProperties) {
        return new Binding(
                rabbitProperties.getCouponReleaseDelayQueue(),
                Binding.DestinationType.QUEUE,
                rabbitProperties.getCouponEventExchange(),
                rabbitProperties.getCouponReleaseDelayRoutingKey(),
                null
        );
    }


    /**
     * 死信队列
     *
     * @param rabbitProperties rabbit 配置参数
     * @return 死信队列
     */
    @Bean
    public Queue couponReleaseQueue(RabbitProperties rabbitProperties) {
        return new Queue(
                rabbitProperties.getCouponReleaseQueue(),
                true,
                false,
                false
        );
    }

    /**
     * 绑定 死信队列和交换机
     *
     * @param rabbitProperties rabbit 配置参数
     * @return Binding
     */
    @Bean
    public Binding couponReleaseBinding(RabbitProperties rabbitProperties) {
        return new Binding(
                rabbitProperties.getCouponReleaseQueue(),
                Binding.DestinationType.QUEUE,
                rabbitProperties.getCouponEventExchange(),
                rabbitProperties.getCouponReleaseRoutingKey(),
                null
        );
    }


}
