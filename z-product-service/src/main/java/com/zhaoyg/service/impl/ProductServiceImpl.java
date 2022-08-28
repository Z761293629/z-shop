package com.zhaoyg.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhaoyg.config.RabbitProductProperties;
import com.zhaoyg.entity.ProductMessage;
import com.zhaoyg.enums.BizCodeEnum;
import com.zhaoyg.enums.ProductOrderStateEnum;
import com.zhaoyg.enums.StockTaskStateEnum;
import com.zhaoyg.exception.BizException;
import com.zhaoyg.feign.ProductOrderFeignService;
import com.zhaoyg.model.entity.Product;
import com.zhaoyg.mapper.ProductMapper;
import com.zhaoyg.model.entity.ProductTask;
import com.zhaoyg.request.LockProductRequest;
import com.zhaoyg.request.OrderItemRequest;
import com.zhaoyg.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhaoyg.service.ProductTaskService;
import com.zhaoyg.util.Result;
import com.zhaoyg.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhao
 * @since 2022-08-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final ProductTaskService productTaskService;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitProductProperties rabbitProperties;
    private final ProductOrderFeignService productOrderFeignService;

    @Override
    public Result page(Integer page, Integer size) {
        Page<Product> productPage = page(Page.of(page, size), Wrappers.<Product>lambdaQuery().orderByDesc(Product::getCreateTime));
        Map<String, Object> pageMap = new HashMap<>(4);
        pageMap.put("total", productPage.getTotal());
        pageMap.put("current", productPage.getCurrent());
        List<ProductVO> records = Optional.ofNullable(productPage.getRecords())
                .stream()
                .flatMap(Collection::stream)
                .map(this::entityToVO)
                .collect(Collectors.toList());
        pageMap.put("records", records);
        return Result.success(pageMap);
    }

    @Override
    public Result detail(Long id) {
        Product product = getById(id);
        if (Objects.isNull(product)) {
            return Result.fail(BizCodeEnum.PRODUCT_NOT_EXIST);
        }
        return Result.success(entityToVO(product));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result lockProducts(LockProductRequest lockProductRequest) {
        String orderOutTradeNo = lockProductRequest.getOrderOutTradeNo();
        List<OrderItemRequest> orderItems = lockProductRequest.getOrderItems();
        List<Long> productIds = orderItems.stream().map(OrderItemRequest::getProductId).collect(Collectors.toList());
        Map<Long, Product> productMap = listByIds(productIds).stream().collect(Collectors.toMap(Product::getId, Function.identity()));
        for (OrderItemRequest orderItem : orderItems) {
            Long productId = orderItem.getProductId();
            Integer buyNum = orderItem.getBuyNum();
            Integer row = baseMapper.lockProductStock(productId, buyNum);
            if (!Objects.equals(row, 1)) {
                throw new BizException(BizCodeEnum.PRODUCT_STOCK_LOCK_FAIL);
            }
            ProductTask productTask = ProductTask.builder()
                    .outTradeNo(orderOutTradeNo)
                    .productId(productId)
                    .productTitle(productMap.get(productId).getTitle())
                    .buyNum(buyNum)
                    .lockState(StockTaskStateEnum.LOCK.name())
                    .createTime(LocalDateTime.now())
                    .build();
            productTaskService.save(productTask);
            // 发送MQ消息到延时队列
            ProductMessage message = ProductMessage.builder().productTaskId(productTask.getId()).orderOutTradeNo(orderOutTradeNo).build();
            rabbitTemplate.convertAndSend(rabbitProperties.getEventExchange(), rabbitProperties.getReleaseDelayRoutingKey(), message);
        }
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean releaseProductStock(ProductMessage productMessage) {
        Long productTaskId = productMessage.getProductTaskId();
        String orderOutTradeNo = productMessage.getOrderOutTradeNo();
        ProductTask productTask = productTaskService.getById(productTaskId);
        if (Objects.isNull(productTask)) {
            log.warn("[商品库存锁定] 不存在 message=[{}]", productMessage);
            return true;
        }
        if (Objects.equals(productTask.getLockState(), StockTaskStateEnum.LOCK.name())) {
            Result result = productOrderFeignService.queryProductOrderState(orderOutTradeNo);
            if (!result.ok()) {
                log.error("[查询订单状态] 失败 message=[{}]", result.getMessage());
                return false;
            }
            String orderState = (String) result.getData();
            if (ProductOrderStateEnum.NEW.name().equalsIgnoreCase(orderState)) {
                log.warn("[商品库存锁定] 订单为NEW message=[{}]", productMessage);
                return false;
            }
            if (ProductOrderStateEnum.PAY.name().equalsIgnoreCase(orderState)) {
                log.info("[商品库存锁定] 订单为PAY 修改锁定记录为FINISH message=[{}]", productMessage);
                productTaskService.updateLockState(productTaskId, StockTaskStateEnum.FINISH);
                return true;
            }
            log.info("[商品库存锁定] 订单不存在 或者为CANCEL 修改锁定记录为CANCEL 并恢复库存 message=[{}]", productMessage);
            productTaskService.updateLockState(productTaskId, StockTaskStateEnum.CANCEL);
            baseMapper.unlockProductStock(productTask.getProductId(), productTask.getBuyNum());
            return true;
        }
        return true;
    }

    private ProductVO entityToVO(Product product) {
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(product, productVO);
        productVO.setStock(productVO.getStock() - product.getLockStock());
        return productVO;
    }
}
