package com.zhaoyg.mapper;

import com.zhaoyg.model.entity.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhao
 * @since 2022-08-17
 */
public interface ProductMapper extends BaseMapper<Product> {

    Integer lockProductStock(@Param("productId") Long productId, @Param("buyNum") Integer buyNum);

    void unlockProductStock(@Param("productId") Long productId, @Param("buyNum") Integer buyNum);
}
