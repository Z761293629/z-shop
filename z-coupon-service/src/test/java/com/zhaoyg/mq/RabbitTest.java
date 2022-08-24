package com.zhaoyg.mq;

import com.zhaoyg.CouponServiceBootTest;
import com.zhaoyg.config.RabbitProperties;
import com.zhaoyg.entity.CouponRecordMessage;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhao
 * @date 2022/8/21
 */
class RabbitTest extends CouponServiceBootTest {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    RabbitProperties rabbitProperties;

    @Test
    void test() {
        rabbitTemplate.convertAndSend(rabbitProperties.getCouponEventExchange(), rabbitProperties.getCouponReleaseDelayRoutingKey(), "test");
    }

    @Test
    void testRelease() {
        CouponRecordMessage message = new CouponRecordMessage();
        message.setCouponTaskId(1L);
        message.setOrderTradeOutNo("123456abc");

        rabbitTemplate.convertAndSend(rabbitProperties.getCouponEventExchange(), rabbitProperties.getCouponReleaseDelayRoutingKey(), message);
    }
}
