package com.zhaoyg.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhaoyg.entity.LoginUser;
import com.zhaoyg.enums.BizCodeEnum;
import com.zhaoyg.interceptor.AuthInterceptor;
import com.zhaoyg.model.entity.CouponRecord;
import com.zhaoyg.mapper.CouponRecordMapper;
import com.zhaoyg.service.CouponRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhaoyg.util.Result;
import com.zhaoyg.vo.CouponRecordVO;
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
 * @since 2022-08-14
 */
@Service
public class CouponRecordServiceImpl extends ServiceImpl<CouponRecordMapper, CouponRecord> implements CouponRecordService {

    @Override
    public Result page(Integer page, Integer size) {
        LoginUser loginUser = AuthInterceptor.loginUserThreadLocal.get();
        Page<CouponRecord> recordPage = page(Page.of(page, size),
                Wrappers.<CouponRecord>lambdaQuery().eq(CouponRecord::getUserId, loginUser.getId()).orderByDesc(CouponRecord::getCreateTime)
        );
        Map<String, Object> pageMap = new HashMap<>(4);
        pageMap.put("total", recordPage.getTotal());
        pageMap.put("current", recordPage.getCurrent());
        List<CouponRecordVO> couponVOList = Optional.ofNullable(recordPage.getRecords())
                .stream()
                .flatMap(Collection::stream)
                .map(this::entityToVo)
                .collect(Collectors.toList());
        pageMap.put("records", couponVOList);
        return Result.success(pageMap);
    }

    @Override
    public Result detail(Long recordId) {
        // 代入用户id作为查询条件 防止水平越权攻击
        LoginUser loginUser = AuthInterceptor.loginUserThreadLocal.get();
        Long userId = loginUser.getId();
        CouponRecord couponRecord = getOne(Wrappers.<CouponRecord>lambdaQuery().eq(CouponRecord::getId, recordId).eq(CouponRecord::getUserId, userId));
        return Objects.isNull(couponRecord) ? Result.fail(BizCodeEnum.COUPON_RECORD_DONT_EXIST) : Result.success(entityToVo(couponRecord));
    }

    private CouponRecordVO entityToVo(CouponRecord couponRecord) {
        CouponRecordVO couponRecordVO = new CouponRecordVO();
        BeanUtils.copyProperties(couponRecord, couponRecordVO);
        return couponRecordVO;
    }
}
