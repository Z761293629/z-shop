package com.zhaoyg.exception;

import com.zhaoyg.enums.BizCodeEnum;
import lombok.Data;
import lombok.Getter;

/**
 * @author zhao
 * @date 2022/8/11
 */
public class BizException extends RuntimeException {

    @Getter
    private final int code;
    @Getter
    private final String message;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BizException(BizCodeEnum codeEnum) {
        this(codeEnum.getCode(), codeEnum.getMessage());
    }
}
