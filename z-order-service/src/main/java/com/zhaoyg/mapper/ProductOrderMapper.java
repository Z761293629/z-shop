package com.zhaoyg.mapper;

import com.zhaoyg.model.entity.ProductOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhao
 * @since 2022-08-20
 */
public interface ProductOrderMapper extends BaseMapper<ProductOrder> {

    void updateState(@Param("orderTradeOutNo") String orderTradeOutNo, @Param("newState") String newState, @Param("oldState") String oldState);
}
