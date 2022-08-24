package com.zhaoyg.mq;

import com.rabbitmq.client.Channel;
import com.zhaoyg.entity.CartItemMessage;
import com.zhaoyg.enums.BizCodeEnum;
import com.zhaoyg.feign.ProductOrderFeignService;
import com.zhaoyg.service.CartService;
import com.zhaoyg.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

/**
 * @author zhao
 * @date 2022/8/21
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(queues = {"${rabbit.cart.release-queue}"})
public class CartTaskMQListener {

    private final CartService cartService;

    private final ProductOrderFeignService productOrderFeignService;

    @RabbitHandler
    public void handlerCouponTask(CartItemMessage cartMessage, Message message, Channel channel) {
        log.info("[购物车] 接收MQ中消息 message=[{}]", cartMessage);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        Result result = productOrderFeignService.queryProductOrderState(cartMessage.getOrderTradeOutNo());
        boolean flag = Objects.equals(result.getCode(), BizCodeEnum.ORDER_DONT_EXIST.getCode());
        try {
            if (flag) {
                cartService.recoverItem(cartMessage);
                channel.basicAck(deliveryTag, false);
            } else {
                channel.basicReject(deliveryTag, true);
            }
        } catch (IOException e) {
            log.error("[购物车] MQ消费错误 message=[{}]", cartMessage, e);
        }
    }


}
