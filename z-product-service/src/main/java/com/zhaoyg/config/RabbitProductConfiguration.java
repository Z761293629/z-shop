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
@EnableConfigurationProperties(RabbitProductProperties.class)
public class RabbitProductConfiguration {

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
    public Exchange productExchange(RabbitProductProperties rabbitProperties) {
        return new TopicExchange(rabbitProperties.getEventExchange(), true, false);
    }

    /**
     * 延迟队列
     *
     * @param rabbitProperties rabbit 配置参数
     * @return 延迟队列
     */
    @Bean
    public Queue productReleaseDelayQueue(RabbitProductProperties rabbitProperties) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("x-message-ttl", rabbitProperties.getTtl());
        paramMap.put("x-dead-letter-exchange", rabbitProperties.getEventExchange());
        paramMap.put("x-dead-letter-routing-key", rabbitProperties.getReleaseRoutingKey());
        return new Queue(
                rabbitProperties.getReleaseDelayQueue(),
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
    public Binding productReleaseDelayBinding(RabbitProductProperties rabbitProperties) {
        return new Binding(
                rabbitProperties.getReleaseDelayQueue(),
                Binding.DestinationType.QUEUE,
                rabbitProperties.getEventExchange(),
                rabbitProperties.getReleaseDelayRoutingKey(),
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
    public Queue productReleaseQueue(RabbitProductProperties rabbitProperties) {
        return new Queue(
                rabbitProperties.getReleaseQueue(),
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
    public Binding productReleaseBinding(RabbitProductProperties rabbitProperties) {
        return new Binding(
                rabbitProperties.getReleaseQueue(),
                Binding.DestinationType.QUEUE,
                rabbitProperties.getEventExchange(),
                rabbitProperties.getReleaseRoutingKey(),
                null
        );
    }


}
