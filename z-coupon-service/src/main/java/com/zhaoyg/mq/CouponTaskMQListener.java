package com.zhaoyg.mq;

import com.rabbitmq.client.Channel;
import com.zhaoyg.entity.CouponRecordMessage;
import com.zhaoyg.service.CouponRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author zhao
 * @date 2022/8/21
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(queues = {"${rabbit.coupon-release-queue}"})
public class CouponTaskMQListener {

    private final CouponRecordService couponRecordService;


    @RabbitHandler
    public void handlerCouponTask(CouponRecordMessage recordMessage, Message message, Channel channel) {
        log.info("[优惠券锁定记录] 接收MQ中消息 message=[{}]", recordMessage);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        boolean flag = couponRecordService.releaseCouponRecord(recordMessage);
        try {
            if (flag) {
                channel.basicAck(deliveryTag, false);
            } else {
                channel.basicReject(deliveryTag, true);
            }
        } catch (IOException e) {
            log.error("[优惠券锁定记录] MQ消费错误 message=[{}]", recordMessage, e);
        }
    }

    @RabbitHandler
    public void handlerCouponTask(String recordMessage, Message message, Channel channel) {
        log.info("[优惠券锁定记录] 接收MQ中消息 message=[{}]", recordMessage);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            log.error("[优惠券锁定记录] MQ消费错误 message=[{}]", recordMessage, e);
        }
    }


}
