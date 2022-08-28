package com.zhaoyg.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhaoyg.component.PayDelegate;
import com.zhaoyg.component.PayInfo;
import com.zhaoyg.config.RabbitOrderProperties;
import com.zhaoyg.constant.CacheKey;
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
import com.zhaoyg.request.*;
import com.zhaoyg.service.ProductOrderItemService;
import com.zhaoyg.service.ProductOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhaoyg.util.Result;
import com.zhaoyg.vo.CouponRecordVO;
import com.zhaoyg.vo.ProductOrderAddressVO;
import com.zhaoyg.vo.ProductOrderItemVO;
import com.zhaoyg.vo.ProductOrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private final PayDelegate payDelegate;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String LUA_SCRIPT = "if ( redis.call('get',KEYS[1]) == ARGV[1] ) " +
            "then return redis.call('del',KEYS[1]) " +
            "else return 0 " +
            "end ";

    @Value("${pay.ali.callback-url}")
    private String notifyUrl;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Result queryProductOrderState(String orderOutTradeNo) {
        ProductOrder productOrder = getOne(Wrappers.<ProductOrder>lambdaQuery().select(ProductOrder::getState).eq(ProductOrder::getOutTradeNo, orderOutTradeNo));
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
    @Transactional(rollbackFor = Exception.class)
    public Result confirmOrder(ConfirmOrderRequest confirmOrderRequest) {
        String orderOutTradeNo = RandomUtil.randomString(32);
        LoginUser loginUser = AuthInterceptor.loginUserThreadLocal.get();
        // 0. 验证确认订单token

        Long redisResult = redisTemplate.execute(new DefaultRedisScript<>(LUA_SCRIPT, Long.class), List.of(String.format(CacheKey.ORDER_TOKEN_KEN, loginUser.getId())), confirmOrderRequest.getToken());
        if (Objects.equals(redisResult, 0L)) {
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_TOKEN_EQUAL_FAIL);
        }


        // 1. 收货地址
        ProductOrderAddressVO address = getUserAddress(confirmOrderRequest.getAddressId());
        // 2. 清除购物车
        Result result = cartFeignService.confirmOrderCartItems(confirmOrderRequest.getProductIdList(), orderOutTradeNo);
        List<ProductOrderItemVO> orderItems = result.getData(new TypeReference<>() {
        });
        if (CollUtil.isEmpty(orderItems)) {
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_CART_ITEM_DONT_EXIST);
        }
        // 3. 验价
        checkAmount(orderItems, confirmOrderRequest);
        // 4. 锁定优惠券
        lockCouponRecords(confirmOrderRequest.getCouponRecordId(), orderOutTradeNo);
        // 5. 锁定库存
        lockProducts(orderItems, orderOutTradeNo);
        // 6. 订单入库
        ProductOrder productOrder = saveProductOrder(orderOutTradeNo, confirmOrderRequest, address, loginUser);
        // 7. 订单项入库
        saveProductOrderItems(orderOutTradeNo, productOrder, orderItems);
        // 8. 发送mq，用于自动关单
        sendDelayMessage(orderOutTradeNo, confirmOrderRequest.getPayType());
        // 9. 创建支付
        PayInfo payInfo = new PayInfo(
                orderOutTradeNo,
                confirmOrderRequest.getPayType(),
                confirmOrderRequest.getRealPayAmount().toEngineeringString(),
                orderItems.stream().map(ProductOrderItemVO::getProductTitle).collect(Collectors.joining("、")),
                null,
                confirmOrderRequest.getClientType(),
                LocalDateTime.now().plusMinutes(30L).format(DATE_TIME_FORMATTER),
                notifyUrl);
        return Result.success(payDelegate.pay(payInfo));
    }

    @Override
    public boolean closeProductOrder(ProductOrderMessage productOrderMessage) {
        String orderOutTradeNo = productOrderMessage.getOrderOutTradeNo();
        ProductOrder productOrder = getOne(Wrappers.<ProductOrder>lambdaQuery().eq(ProductOrder::getOutTradeNo, orderOutTradeNo));
        if (Objects.isNull(productOrder)) {
            log.info("[定时关单] 订单为空 直接确认消息 message=[{}]", productOrderMessage);
            return true;
        }
        if (ProductOrderStateEnum.PAY.name().equalsIgnoreCase(productOrder.getState())) {
            log.info("[定时关单] 订单已支付 直接确认消息 message=[{}]", productOrderMessage);
            return true;
        }

        String payResult = payDelegate.payState(orderOutTradeNo, productOrderMessage.getPayType());

        if (StrUtil.isBlank(payResult)) {
            log.info("[定时关单] 查询第三方支付结果为未支付 关闭订单 message=[{}]", productOrderMessage);
            baseMapper.updateState(orderOutTradeNo, ProductOrderStateEnum.CANCEL.name(), ProductOrderStateEnum.NEW.name());
            return true;
        } else {
            log.warn("[定时关单] 查询第三方支付结果为已支付 回调可能有问题 修改订单为PAY message=[{}]", productOrderMessage);
            baseMapper.updateState(orderOutTradeNo, ProductOrderStateEnum.PAY.name(), ProductOrderStateEnum.NEW.name());
            return true;
        }
    }

    @Override
    public void handlePayCallback(ProductOrderPayTypeEnum payTypeEnum, Map<String, String> map) {
        switch (payTypeEnum) {
            case ALI:
                log.info("[处理支付回调] ali paramMap = [{}]", map);
                String outTradeNo = map.get("out_trade_no");
                String tradeStatus = map.get("trade_status");
                if ("TRADE_SUCCESS".equalsIgnoreCase(tradeStatus) || "TRADE_FINISHED".equalsIgnoreCase(tradeStatus)) {
                    baseMapper.updateState(outTradeNo, ProductOrderStateEnum.PAY.name(), ProductOrderStateEnum.NEW.name());
                }
                break;
            case BANK:
                // TODO
                break;
            case WECHAT:
                // TODO
                break;
            default:
                log.warn("[处理支付回调] 不支持的支付类型");
        }

    }

    @Override
    public Result pageOrder(Integer page, Integer size, String state) {
        LoginUser loginUser = AuthInterceptor.loginUserThreadLocal.get();
        LambdaQueryWrapper<ProductOrder> wrapper = Wrappers.<ProductOrder>lambdaQuery().eq(ProductOrder::getUserId, loginUser.getId());
        if (StrUtil.isNotBlank(state)) {
            wrapper.eq(ProductOrder::getState, state);
        }
        Page<ProductOrder> orderPage = page(Page.of(page, size), wrapper);
        List<ProductOrder> records = orderPage.getRecords();
        Map<String, Object> pageMap = new HashMap<>(4);
        pageMap.put("total", orderPage.getTotal());
        pageMap.put("current", orderPage.getCurrent());
        if (CollUtil.isNotEmpty(records)) {
            List<Long> orderIds = records.stream().map(ProductOrder::getId).collect(Collectors.toList());
            List<ProductOrderItem> orderItems = productOrderItemService.listByOrderIds(orderIds);
            Map<Long, List<ProductOrderItem>> itemMap = orderItems.stream().collect(Collectors.groupingBy(ProductOrderItem::getProductOrderId));

            List<ProductOrderVO> orders = records.stream()
                    .map(order -> {
                        ProductOrderVO orderVO = new ProductOrderVO();
                        BeanUtils.copyProperties(order, orderVO);
                        List<ProductOrderItemVO> items = itemMap.get(order.getId())
                                .stream()
                                .map(item -> {
                                    ProductOrderItemVO itemVO = new ProductOrderItemVO();
                                    BeanUtils.copyProperties(item, itemVO);
                                    return itemVO;
                                })
                                .collect(Collectors.toList());
                        orderVO.setOrderItems(items);
                        return orderVO;
                    })
                    .collect(Collectors.toList());
            pageMap.put("records", orders);
        }
        return Result.success(pageMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result repayOrder(RepayOrderRequest repayOrderRequest) {
        String outTradeNo = repayOrderRequest.getOutTradeNo();
        ProductOrder productOrder = getOne(Wrappers.<ProductOrder>lambdaQuery().eq(ProductOrder::getOutTradeNo, outTradeNo));
        if (Objects.isNull(productOrder)) {
            throw new BizException(BizCodeEnum.ORDER_DONT_EXIST);
        }
        if (!Objects.equals(productOrder.getState(), ProductOrderStateEnum.NEW.name())) {
            throw new BizException(BizCodeEnum.ORDER_STATE_ILLEGAL);
        }
        ProductOrder newEntity = productOrder.withPayType(repayOrderRequest.getPayType());
        updateById(newEntity);
        LocalDateTime createTime = productOrder.getCreateTime();
        PayInfo payInfo = new PayInfo(
                outTradeNo,
                repayOrderRequest.getPayType(),
                productOrder.getPayAmount().toEngineeringString(),
                productOrder.getOutTradeNo(),
                null,
                repayOrderRequest.getClientType(),
                createTime.plusMinutes(30).format(DATE_TIME_FORMATTER),
                notifyUrl
        );
        return Result.success(payDelegate.pay(payInfo));
    }

    private void sendDelayMessage(String orderOutTradeNo, String payType) {
        ProductOrderMessage message = new ProductOrderMessage();
        message.setOrderOutTradeNo(orderOutTradeNo);
        message.setPayType(payType);
        rabbitTemplate.convertAndSend(rabbitOrderProperties.getEventExchange(), rabbitOrderProperties.getReleaseDelayRoutingKey(), message);
    }

    private void saveProductOrderItems(String orderOutTradeNo, ProductOrder productOrder, List<ProductOrderItemVO> orderItems) {
        List<ProductOrderItem> productOrderItemList = orderItems.stream()
                .map(orderItem -> {
                    ProductOrderItem productOrderItem = new ProductOrderItem();
                    productOrderItem.setProductOrderId(productOrder.getId());
                    productOrderItem.setOutTradeNo(orderOutTradeNo);
                    productOrderItem.setProductId(orderItem.getProductId());
                    productOrderItem.setProductTitle(orderItem.getProductTitle());
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

    private ProductOrder saveProductOrder(String orderOutTradeNo,
                                          ConfirmOrderRequest confirmOrderRequest,
                                          ProductOrderAddressVO address,
                                          LoginUser loginUser) {
        ProductOrder productOrder = new ProductOrder();
        productOrder.setOutTradeNo(orderOutTradeNo);
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

    private void lockProducts(List<ProductOrderItemVO> orderItems, String orderOutTradeNo) {
        LockProductRequest productRequest = new LockProductRequest();
        List<OrderItemRequest> orderItemRequestList = orderItems.stream().map(orderItem -> {
            OrderItemRequest orderItemRequest = new OrderItemRequest();
            orderItemRequest.setProductId(orderItem.getProductId());
            orderItemRequest.setBuyNum(orderItem.getBuyNum());
            return orderItemRequest;
        }).collect(Collectors.toList());
        productRequest.setOrderItems(orderItemRequestList);
        productRequest.setOrderOutTradeNo(orderOutTradeNo);
        Result result = productFeignService.lockProducts(productRequest);
        if (!result.ok()) {
            throw new BizException(BizCodeEnum.PRODUCT_STOCK_LOCK_FAIL);
        }
    }

    private void lockCouponRecords(Long couponRecordId, String orderOutTradeNo) {
        if (Objects.isNull(couponRecordId) || couponRecordId < 0) {
            return;
        }
        LockCouponRequest lockCouponRequest = new LockCouponRequest();
        lockCouponRequest.setCouponRecordIds(List.of(couponRecordId));
        lockCouponRequest.setOrderOutTradeNo(orderOutTradeNo);
        Result result = couponRecordFeignService.lockCouponRecords(lockCouponRequest);
        if (!result.ok()) {
            throw new BizException(BizCodeEnum.COUPON_RECORD_LOCK_FAIL);
        }
    }

    private void checkAmount(List<ProductOrderItemVO> orderItems, ConfirmOrderRequest confirmOrderRequest) {
        BigDecimal realPay = orderItems.stream().map(ProductOrderItemVO::getTotalAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        Long couponRecordId = confirmOrderRequest.getCouponRecordId();
        if (Objects.nonNull(couponRecordId) && couponRecordId > 0) {

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
            log.error("[获取地址] 失败 message=[{}]", result.getMessage());
            throw new BizException(BizCodeEnum.ADDRESS_DONT_EXIST);
        }
        return result.getData(new TypeReference<>() {
        });
    }
}
