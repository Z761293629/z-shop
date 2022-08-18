package com.zhaoyg.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhaoyg.model.entity.Product;
import com.zhaoyg.mapper.ProductMapper;
import com.zhaoyg.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhaoyg.util.Result;
import com.zhaoyg.vo.ProductVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhao
 * @since 2022-08-17
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

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
        return Result.success(entityToVO(product));
    }

    private ProductVO entityToVO(Product product) {
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(product, productVO);
        productVO.setStock(productVO.getStock() - product.getLockStock());
        return productVO;
    }
}
