package com.zhaoyg.service.impl;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhaoyg.config.RabbitProperties;
import com.zhaoyg.entity.CouponRecordMessage;
import com.zhaoyg.entity.LoginUser;
import com.zhaoyg.enums.BizCodeEnum;
import com.zhaoyg.enums.StockTaskStateEnum;
import com.zhaoyg.enums.CouponUseStateEnum;
import com.zhaoyg.enums.ProductOrderStateEnum;
import com.zhaoyg.exception.BizException;
import com.zhaoyg.feign.ProductOrderFeignService;
import com.zhaoyg.interceptor.AuthInterceptor;
import com.zhaoyg.model.entity.CouponRecord;
import com.zhaoyg.mapper.CouponRecordMapper;
import com.zhaoyg.model.entity.CouponTask;
import com.zhaoyg.request.LockCouponRequest;
import com.zhaoyg.service.CouponRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhaoyg.service.CouponTaskService;
import com.zhaoyg.util.Result;
import com.zhaoyg.vo.CouponRecordVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponRecordServiceImpl extends ServiceImpl<CouponRecordMapper, CouponRecord> implements CouponRecordService {
    private final CouponTaskService couponTaskService;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitProperties rabbitProperties;
    private final ProductOrderFeignService productOrderFeignService;

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

    /**
     * 锁定优惠券记录
     *
     * @param lockCouponRequest 锁定优惠券记录请求
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result lockCouponRecords(LockCouponRequest lockCouponRequest) {
        // 1. 锁定优惠券记录
        LoginUser loginUser = AuthInterceptor.loginUserThreadLocal.get();
        List<Long> couponRecordIds = lockCouponRequest.getCouponRecordIds();
        Integer row = baseMapper.lockUseStateBatch(loginUser.getId(), CouponUseStateEnum.USED.name(), couponRecordIds);
        if (!Objects.equals(row, couponRecordIds.size())) {
            log.error("[锁定优惠券] 更新优惠券记录状态失败 row=[{}]", row);
            throw new BizException(BizCodeEnum.COUPON_RECORD_LOCK_FAIL);
        }
        // 2. 插入优惠券任务
        String orderOutTradeNo = lockCouponRequest.getOrderOutTradeNo();
        List<CouponTask> couponTaskList = couponRecordIds.stream().map(couponRecordId ->
                        CouponTask.builder()
                                .couponRecordId(couponRecordId)
                                .outTradeNo(orderOutTradeNo)
                                .lockState(StockTaskStateEnum.LOCK.name())
                                .createTime(LocalDateTime.now())
                                .build()
                )
                .collect(Collectors.toList());
        row = couponTaskService.insertBatch(couponTaskList);
        if (!Objects.equals(row, couponRecordIds.size())) {
            log.error("[锁定优惠券] 插入优惠券记录任务失败 row=[{}]", row);
            throw new BizException(BizCodeEnum.COUPON_RECORD_LOCK_FAIL);
        }
        // 3.记录发送至延时队列
        for (CouponTask couponTask : couponTaskList) {
            CouponRecordMessage message = CouponRecordMessage.builder().couponTaskId(couponTask.getId()).orderOutTradeNo(orderOutTradeNo).build();
            rabbitTemplate.convertAndSend(rabbitProperties.getCouponEventExchange(), rabbitProperties.getCouponReleaseDelayRoutingKey(), message);
            log.debug("[优惠券锁定记录] 发送至MQ中,message=[{}]", message);
        }
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean releaseCouponRecord(CouponRecordMessage recordMessage) {
        Long couponTaskId = recordMessage.getCouponTaskId();
        CouponTask couponTask = couponTaskService.getById(couponTaskId);
        if (Objects.isNull(couponTask)) {
            log.warn("[优惠券锁定记录] 不存在 message=[{}]", recordMessage);
            return true;
        }
        // 锁定记录为 LOCK 才进行处理
        if (couponTask.getLockState().equalsIgnoreCase(StockTaskStateEnum.LOCK.name())) {
            Result result = productOrderFeignService.queryProductOrderState(recordMessage.getOrderOutTradeNo());
            if (!result.ok()) {
                log.error("[查询订单状态] 失败 message=[{}]", result.getMessage());
                return false;
            }
            String orderState = (String) result.getData();
            if (ProductOrderStateEnum.NEW.name().equalsIgnoreCase(orderState)) {
                log.warn("[优惠券锁定记录] 订单为NEW message=[{}]", recordMessage);
                return false;
            }
            if (ProductOrderStateEnum.PAY.name().equalsIgnoreCase(orderState)) {
                log.info("[优惠券锁定记录] 订单为PAY 修改锁定记录为FINISH message=[{}]", recordMessage);
                couponTaskService.updateLockState(couponTaskId, StockTaskStateEnum.FINISH);
                return true;
            }
            log.info("[优惠券锁定记录] 订单为CANCEL 修改锁定记录为CANCEL 并恢复优惠券记录 message=[{}]", recordMessage);
            couponTaskService.updateLockState(couponTaskId, StockTaskStateEnum.CANCEL);
            baseMapper.updateUseState(couponTask.getCouponRecordId(), CouponUseStateEnum.NEW.name());
            return true;
        }

        // 锁定记录 非LOCK 直接true 消费
        return true;
    }


    private CouponRecordVO entityToVo(CouponRecord couponRecord) {
        CouponRecordVO couponRecordVO = new CouponRecordVO();
        BeanUtils.copyProperties(couponRecord, couponRecordVO);
        return couponRecordVO;
    }
}
