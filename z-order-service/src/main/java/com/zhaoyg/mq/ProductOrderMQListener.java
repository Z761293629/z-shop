package com.zhaoyg.mq;

import com.rabbitmq.client.Channel;
import com.zhaoyg.entity.ProductOrderMessage;
import com.zhaoyg.service.ProductOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author zhao
 * @date 2022/8/24
 */
@Slf4j
@Component
@RabbitListener(queues = {"${rabbit.order.release-queue}"})
@RequiredArgsConstructor
public class ProductOrderMQListener {
    private final ProductOrderService productOrderService;

    @RabbitHandler
    public void closeProductOrder(ProductOrderMessage productOrderMessage, Message message, Channel channel) {
        log.info("[定时关单] 接收MQ中消息 message = [{}]", productOrderMessage);
        boolean flag = productOrderService.closeProductOrder(productOrderMessage);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            if (flag) {
                channel.basicAck(deliveryTag, false);
            } else {
                channel.basicReject(deliveryTag, true);
            }
        } catch (IOException e) {
            log.error("[定时关单] 异常 message = [{}]", productOrderMessage, e);
        }
    }
}
