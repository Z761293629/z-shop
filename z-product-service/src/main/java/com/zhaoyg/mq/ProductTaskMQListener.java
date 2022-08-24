package com.zhaoyg.mq;

import com.rabbitmq.client.Channel;
import com.zhaoyg.entity.ProductMessage;
import com.zhaoyg.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author zhao
 * @date 2022/8/21
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(queues = {"${rabbit.product.release-queue}"})
public class ProductTaskMQListener {

    private final ProductService productService;


    @RabbitHandler
    public void handlerCouponTask(ProductMessage productMessage, Message message, Channel channel) {
        log.info("[商品库存锁定记录] 接收MQ中消息 message=[{}]", productMessage);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        boolean flag = productService.releaseProductStock(productMessage);
        try {
            if (flag) {
                channel.basicAck(deliveryTag, false);
            } else {
                channel.basicReject(deliveryTag, true);
            }
        } catch (IOException e) {
            log.error("[商品库存锁定记录] MQ消费错误 message=[{}]", productMessage, e);
        }
    }

    @RabbitHandler
    public void handlerCouponTask(String recordMessage, Message message, Channel channel) {
        log.info("[商品库存锁定记录] 接收MQ中消息 message=[{}]", recordMessage);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            log.error("[商品库存锁定记录] MQ消费错误 message=[{}]", recordMessage, e);
        }
    }


}
