package com.zhaoyg.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhaoyg.config.RabbitOrderProperties;
import com.zhaoyg.entity.LoginUser;
import com.zhaoyg.entity.ProductOrderMessage;
import com.zhaoyg.enums.*;
import com.zhaoyg.exception.BizException;
import com.zhaoyg.feign.CartFeignService;
import com.zhaoyg.feign.CouponRecordFeignService;
import com.zhaoyg.feign.ProductFeignService;
import com.zhaoyg.feign.UserAddressFeignService;
import com.zhaoyg.interceptor.AuthInterceptor;
import com.zhaoyg.model.entity.ProductOrder;
import com.zhaoyg.mapper.ProductOrderMapper;
import com.zhaoyg.model.entity.ProductOrderItem;
import com.zhaoyg.request.ConfirmOrderRequest;
import com.zhaoyg.request.LockCouponRequest;
import com.zhaoyg.request.LockProductRequest;
import com.zhaoyg.request.OrderItemRequest;
import com.zhaoyg.service.ProductOrderItemService;
import com.zhaoyg.service.ProductOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhaoyg.util.Result;
import com.zhaoyg.vo.CouponRecordVO;
import com.zhaoyg.vo.ProductOrderAddressVO;
import com.zhaoyg.vo.ProductOrderItemVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhao
 * @since 2022-08-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductOrderServiceImpl extends ServiceImpl<ProductOrderMapper, ProductOrder> implements ProductOrderService {
    private final UserAddressFeignService userAddressFeignService;
    private final CartFeignService cartFeignService;
    private final CouponRecordFeignService couponRecordFeignService;
    private final ProductFeignService productFeignService;
    private final ProductOrderItemService productOrderItemService;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitOrderProperties rabbitOrderProperties;

    @Override
    public Result queryProductOrderState(String orderTradeOutNo) {
        ProductOrder productOrder = getOne(Wrappers.<ProductOrder>lambdaQuery().select(ProductOrder::getState).eq(ProductOrder::getOutTradeNo, orderTradeOutNo));
        return Optional.ofNullable(productOrder).map(order -> Result.success(order.getState())).orElse(Result.fail(BizCodeEnum.ORDER_DONT_EXIST));
    }

    /**
     * 确认订单
     * <p/>
     * 1. 获取收货地址详情
     *
     * @param confirmOrderRequest 确认订单请求
     * @return 操作结果
     */
    @Override
    public Result confirmOrder(ConfirmOrderRequest confirmOrderRequest) {
        String orderTradeOutNo = RandomUtil.randomString(32);
        LoginUser loginUser = AuthInterceptor.loginUserThreadLocal.get();

        // 1. 收货地址
        ProductOrderAddressVO address = getUserAddress(confirmOrderRequest.getAddressId());
        // 2. 清除购物车
        Result result = cartFeignService.confirmOrderCartItems(confirmOrderRequest.getProductIdList(), orderTradeOutNo);
        List<ProductOrderItemVO> orderItems = result.getData(new TypeReference<>() {
        });
        if (CollUtil.isEmpty(orderItems)) {
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_CART_ITEM_DONT_EXIST);
        }
        // 3. 验价
        checkAmount(orderItems, confirmOrderRequest);
        // 4. 锁定优惠券
        lockCouponRecords(confirmOrderRequest.getCouponRecordId(), orderTradeOutNo);
        // 5. 锁定库存
        lockProducts(orderItems, orderTradeOutNo);
        // 6. 订单入库
        ProductOrder productOrder = saveProductOrder(orderTradeOutNo, confirmOrderRequest, address, loginUser);
        // 7. 订单项入库
        saveProductOrderItems(orderTradeOutNo, productOrder, orderItems);
        // 8. 发送mq，用于自动关单
        sendDelayMessage(orderTradeOutNo);


        return null;
    }

    @Override
    public boolean closeProductOrder(ProductOrderMessage productOrderMessage) {
        String orderTradeOutNo = productOrderMessage.getOrderTradeOutNo();
        ProductOrder productOrder = getOne(Wrappers.<ProductOrder>lambdaQuery().eq(ProductOrder::getOutTradeNo, orderTradeOutNo));
        if (Objects.isNull(productOrder)) {
            log.info("[定时关单] 订单为空 直接确认消息 message=[{}]", productOrderMessage);
            return true;
        }
        if (ProductOrderStateEnum.PAY.name().equalsIgnoreCase(productOrder.getState())) {
            log.info("[定时关单] 订单已支付 直接确认消息 message=[{}]", productOrderMessage);
            return true;
        }

        String payResult = "";

        if (StrUtil.isBlank(payResult)) {
            log.info("[定时关单] 查询第三方支付结果为未支付 关闭订单 message=[{}]", productOrderMessage);
            baseMapper.updateState(orderTradeOutNo, ProductOrderStateEnum.CANCEL.name(), ProductOrderStateEnum.NEW.name());
            return true;
        } else {
            log.warn("[定时关单] 查询第三方支付结果为已支付 回调可能有问题 修改订单为PAY message=[{}]", productOrderMessage);
            baseMapper.updateState(orderTradeOutNo, ProductOrderStateEnum.PAY.name(), ProductOrderStateEnum.NEW.name());
            return true;
        }
    }

    private void sendDelayMessage(String orderTradeOutNo) {
        ProductOrderMessage message = new ProductOrderMessage();
        message.setOrderTradeOutNo(orderTradeOutNo);
        rabbitTemplate.convertAndSend(rabbitOrderProperties.getEventExchange(), rabbitOrderProperties.getReleaseDelayRoutingKey(), message);
    }

    private void saveProductOrderItems(String orderTradeOutNo, ProductOrder productOrder, List<ProductOrderItemVO> orderItems) {
        List<ProductOrderItem> productOrderItemList = orderItems.stream()
                .map(orderItem -> {
                    ProductOrderItem productOrderItem = new ProductOrderItem();
                    productOrderItem.setProductOrderId(productOrder.getId());
                    productOrderItem.setOutTradeNo(orderTradeOutNo);
                    productOrderItem.setProductId(orderItem.getProduceId());
                    productOrderItem.setProductName(orderItem.getProductTitle());
                    productOrderItem.setProductImg(orderItem.getProductImg());
                    productOrderItem.setBuyNum(orderItem.getBuyNum());
                    productOrderItem.setCreateTime(LocalDateTime.now());
                    productOrderItem.setTotalAmount(orderItem.getTotalAmount());
                    productOrderItem.setAmount(orderItem.getAmount());
                    return productOrderItem;
                })
                .collect(Collectors.toList());
        productOrderItemService.saveBatch(productOrderItemList);
    }

    private ProductOrder saveProductOrder(String orderTradeOutNo,
                                          ConfirmOrderRequest confirmOrderRequest,
                                          ProductOrderAddressVO address,
                                          LoginUser loginUser) {
        ProductOrder productOrder = new ProductOrder();
        productOrder.setOutTradeNo(orderTradeOutNo);
        productOrder.setState(ProductOrderStateEnum.NEW.name());
        productOrder.setCreateTime(LocalDateTime.now());
        productOrder.setTotalAmount(confirmOrderRequest.getTotalAmount());
        productOrder.setPayAmount(confirmOrderRequest.getRealPayAmount());
        productOrder.setPayType(confirmOrderRequest.getPayType());
        productOrder.setNickname(loginUser.getName());
        productOrder.setHeadImg(loginUser.getHeadImg());
        productOrder.setUserId(loginUser.getId());
        productOrder.setDel(0);
        productOrder.setUpdateTime(LocalDateTime.now());
        productOrder.setOrderType(ProductOrderTypeEnum.DAILY.name());
        productOrder.setReceiverAddress(JSON.toJSONString(address));
        save(productOrder);
        return productOrder;
    }

    private void lockProducts(List<ProductOrderItemVO> orderItems, String orderTradeOutNo) {
        LockProductRequest productRequest = new LockProductRequest();
        List<OrderItemRequest> orderItemRequestList = orderItems.stream().map(orderItem -> {
            OrderItemRequest orderItemRequest = new OrderItemRequest();
            orderItemRequest.setProductId(orderItem.getProduceId());
            orderItemRequest.setBuyNum(orderItem.getBuyNum());
            return orderItemRequest;
        }).collect(Collectors.toList());
        productRequest.setOrderItems(orderItemRequestList);
        productRequest.setOrderTradeOutNo(orderTradeOutNo);
        Result result = productFeignService.lockProducts(productRequest);
        if (!result.ok()) {
            throw new BizException(BizCodeEnum.PRODUCT_STOCK_LOCK_FAIL);
        }
    }

    private void lockCouponRecords(Long couponRecordId, String orderTradeOutNo) {
        if (Objects.isNull(couponRecordId) || couponRecordId < 0) {
            return;
        }
        LockCouponRequest lockCouponRequest = new LockCouponRequest();
        lockCouponRequest.setCouponRecordIds(List.of(couponRecordId));
        lockCouponRequest.setOrderTradeOutNo(orderTradeOutNo);
        Result result = couponRecordFeignService.lockCouponRecords(lockCouponRequest);
        if (!result.ok()) {
            throw new BizException(BizCodeEnum.COUPON_RECORD_LOCK_FAIL);
        }
    }

    private void checkAmount(List<ProductOrderItemVO> orderItems, ConfirmOrderRequest confirmOrderRequest) {
        BigDecimal realPay = orderItems.stream().map(ProductOrderItemVO::getTotalAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        Long couponRecordId = confirmOrderRequest.getCouponRecordId();

        CouponRecordVO couponRecord = getCouponRecord(couponRecordId);
        // 计算下单价格是否满足优惠券满减条件

        // 1. 不满足优惠券使用条件
        if (realPay.compareTo(couponRecord.getConditionPrice()) < 0) {
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_COUPON_FAIL);
        }
        // 2. 满足优惠券使用条件
        if (realPay.compareTo(couponRecord.getPrice()) < 0) {
            realPay = BigDecimal.ZERO;
        } else {
            realPay = realPay.subtract(couponRecord.getPrice());
        }
        // 3. 是否和请求的实际支付价格相同
        if (realPay.compareTo(confirmOrderRequest.getRealPayAmount()) != 0) {
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_COUPON_FAIL);
        }


    }

    private CouponRecordVO getCouponRecord(Long couponRecordId) {
        Result result = couponRecordFeignService.detail(couponRecordId);
        if (!result.ok()) {
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_COUPON_FAIL);
        }
        CouponRecordVO couponRecord = result.getData(new TypeReference<>() {
        });
        // 优惠券使用状态
        if (!CouponUseStateEnum.NEW.name().equalsIgnoreCase(couponRecord.getUseState())) {
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_COUPON_FAIL);
        }
        // 优惠券使用时间
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(couponRecord.getStartTime()) || now.isAfter(couponRecord.getEndTime())) {
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_COUPON_FAIL);
        }
        return couponRecord;
    }

    private ProductOrderAddressVO getUserAddress(Long addressId) {
        Result result = userAddressFeignService.detail(addressId);
        if (!result.ok()) {
            log.error("[获取地址] 失败 messge=[{}]", result.getMessage());
            throw new BizException(BizCodeEnum.ADDRESS_DONT_EXIST);
        }
        return result.getData(new TypeReference<>() {
        });
    }
}
