package com.zhaoyg.service;

import com.zhaoyg.enums.SendCodeEnum;
import com.zhaoyg.util.Result;

/**
 * @author zhao
 * @date 2022/8/12
 */
public interface NotifyService {
    Result sendCode(SendCodeEnum sendCodeEnum, String to);

    boolean checkCode(SendCodeEnum sendCodeEnum, String code, String to);
}
