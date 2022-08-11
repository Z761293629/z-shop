package com.zhaoyg.exception;

import com.zhaoyg.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author zhao
 * @date 2022/8/11
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result handlerException(Exception exception) {
        if (exception instanceof BizException) {
            log.error("[业务异常] ===> ", exception);
            BizException bizException = (BizException) exception;
            return Result.build(bizException.getCode(), bizException.getMessage());
        } else {
            log.error("[未知异常] ===> ", exception);
            return Result.fail("未知异常");
        }
    }
}
