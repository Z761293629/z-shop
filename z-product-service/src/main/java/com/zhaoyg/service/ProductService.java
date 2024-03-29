package com.zhaoyg.service;

import com.zhaoyg.entity.ProductMessage;
import com.zhaoyg.model.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhaoyg.request.LockProductRequest;
import com.zhaoyg.util.Result;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zhao
 * @since 2022-08-17
 */
public interface ProductService extends IService<Product> {

    Result page(Integer page, Integer size);

    Result detail(Long id);

    Result lockProducts(LockProductRequest lockProductRequest);

    boolean releaseProductStock(ProductMessage productMessage);
}
