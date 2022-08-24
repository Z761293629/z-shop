package com.zhaoyg.service;

import com.zhaoyg.entity.CouponRecordMessage;
import com.zhaoyg.model.entity.CouponRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhaoyg.request.LockCouponRequest;
import com.zhaoyg.util.Result;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zhao
 * @since 2022-08-14
 */
public interface CouponRecordService extends IService<CouponRecord> {

    Result page(Integer page, Integer size);

    Result detail(Long recordId);

    Result lockCouponRecords(LockCouponRequest lockCouponRequest);

    boolean releaseCouponRecord(CouponRecordMessage recordMessage);
}
