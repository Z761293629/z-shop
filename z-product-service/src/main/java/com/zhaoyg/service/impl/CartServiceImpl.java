package com.zhaoyg.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.zhaoyg.config.RabbitCartProperties;
import com.zhaoyg.entity.CartItemMessage;
import com.zhaoyg.exception.BizException;
import com.zhaoyg.request.CartItemAddRequest;
import com.zhaoyg.constant.CacheKey;
import com.zhaoyg.enums.BizCodeEnum;
import com.zhaoyg.interceptor.AuthInterceptor;
import com.zhaoyg.model.entity.Product;
import com.zhaoyg.request.CartItemUpdateRequest;
import com.zhaoyg.service.CartService;
import com.zhaoyg.service.ProductService;
import com.zhaoyg.util.Result;
import com.zhaoyg.vo.CartItemVO;
import com.zhaoyg.vo.CartVO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhao
 * @date 2022/8/19
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductService productService;

    private final RabbitTemplate rabbitTemplate;
    private final RabbitCartProperties rabbitCartProperties;

    @Override
    public Result addItem(CartItemAddRequest cartItemAddRequest) {
        Long productId = cartItemAddRequest.getProductId();
        Product product = productService.getById(productId);
        if (Objects.isNull(product)) {
            return Result.fail(BizCodeEnum.PRODUCT_NOT_EXIST);
        }
        Integer buyNum = cartItemAddRequest.getBuyNum();
        addItem(product, buyNum, cartKey());
        return Result.success();
    }

    private void addItem(Product product, Integer buyNum, String cartKey) {
        BoundHashOperations<String, String, CartItemVO> cart = cart(cartKey);
        CartItemVO item = cart.get(String.valueOf(product.getId()));
        if (Objects.isNull(item)) {
            item = new CartItemVO();
            item.setProduceId(product.getId());
            item.setProductTitle(product.getTitle());
            item.setProductImg(product.getCoverImg());
            item.setAmount(product.getAmount());
            item.setBuyNum(buyNum);
        } else {
            item.setBuyNum(item.getBuyNum() + buyNum);
        }
        cart.put(String.valueOf(product.getId()), item);
    }

    @Override
    public Result clearCart() {
        return Result.success(redisTemplate.delete(cartKey()));
    }

    @Override
    public Result myCart(Boolean latestAmount) {

        CartVO cart = new CartVO();
        cart.setCartItems(buildCarItems(latestAmount));
        return Result.success(cart);
    }

    private List<CartItemVO> buildCarItems(Boolean latestAmount) {
        List<CartItemVO> items = cart(cartKey()).values();
        if (Objects.isNull(items) || CollUtil.isEmpty(items)) {
            throw new BizException(BizCodeEnum.CART_EMPTY);
        }
        if (Boolean.TRUE.equals(latestAmount)) {
            updateLatestAmount(items);
        }
        return items;
    }

    @Override
    public Result removeItem(Long productId) {
        Boolean hasKey = cart(cartKey()).hasKey(String.valueOf(productId));
        if (!Boolean.TRUE.equals(hasKey)) {
            return Result.fail(BizCodeEnum.CART_PRODUCT_DONT_EXIT);
        }
        return Result.success(cart(cartKey()).delete(String.valueOf(productId)));
    }

    @Override
    public Result updateItem(CartItemUpdateRequest cartItemUpdateRequest) {
        String productId = String.valueOf(cartItemUpdateRequest.getProductId());
        Integer buyNum = cartItemUpdateRequest.getBuyNum();
        BoundHashOperations<String, String, CartItemVO> cart = cart(cartKey());
        if (!Boolean.TRUE.equals(cart.hasKey(productId))) {
            return Result.fail(BizCodeEnum.CART_PRODUCT_DONT_EXIT);
        }
        CartItemVO item = cart.get(productId);
        item.setBuyNum(buyNum);
        cart.put(productId, item);
        return Result.success();
    }

    @Override
    public List<CartItemVO> confirmOrderCartItems(List<Long> productIds, String orderTradeOutNo) {
        Long userId = AuthInterceptor.loginUserThreadLocal.get().getId();
        List<CartItemVO> carItems = buildCarItems(true);
        return carItems
                .stream()
                .filter(item -> {
                    Long produceId = item.getProduceId();
                    if (productIds.contains(produceId)) {
                        removeItem(produceId);
                        CartItemMessage message = CartItemMessage.builder()
                                .orderTradeOutNo(orderTradeOutNo)
                                .userId(userId)
                                .productId(item.getProduceId())
                                .buyNum(item.getBuyNum())
                                .build();
                        rabbitTemplate.convertAndSend(rabbitCartProperties.getEventExchange(), rabbitCartProperties.getReleaseDelayRoutingKey(), message);
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());

    }

    @Override
    public void recoverItem(CartItemMessage productMessage) {
        Product product = productService.getById(productMessage.getProductId());
        addItem(product, productMessage.getBuyNum(), cartKey(productMessage.getUserId()));
    }

    private void updateLatestAmount(List<CartItemVO> items) {
        Set<String> productIds = cart(cartKey()).keys();
        if (Objects.isNull(productIds) || CollUtil.isEmpty(productIds)) {
            return;
        }
        List<Product> products = productService.listByIds(productIds.stream().map(Long::parseLong).collect(Collectors.toList()));
        Map<Long, BigDecimal> latestAmountMap = products.stream().collect(Collectors.toMap(Product::getId, Product::getAmount));
        items.forEach(item -> item.setAmount(latestAmountMap.get(item.getProduceId())));
    }

    private BoundHashOperations<String, String, CartItemVO> cart(String cartKey) {
        return redisTemplate.boundHashOps(cartKey);
    }

    private String cartKey() {
        Long userId = AuthInterceptor.loginUserThreadLocal.get().getId();
        return cartKey(userId);
    }

    private String cartKey(Long userId) {
        return String.format(CacheKey.CART_KEY, userId);
    }

}
