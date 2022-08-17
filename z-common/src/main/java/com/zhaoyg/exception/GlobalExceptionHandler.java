package com.zhaoyg.exception;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.zhaoyg.enums.BizCodeEnum;
import com.zhaoyg.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

/**
 * @author zhao
 * @date 2022/8/11
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler implements ResponseBodyAdvice<Object> {


    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result handlerException(Exception exception) {
        log.error("error", exception);
        if (exception instanceof MissingServletRequestParameterException) {
            Result result = Result.fail(BizCodeEnum.PARAM_ILLEGAL);
            String msg = MessageFormat.format("缺少参数{0}", ((MissingServletRequestParameterException) exception).getParameterName());
            result.setMessage(msg);
            return result;
        }

        if (exception instanceof ConstraintViolationException) {
            // 单个参数校验异常
            Result result = Result.fail(BizCodeEnum.PARAM_ILLEGAL);
            Set<ConstraintViolation<?>> sets = ((ConstraintViolationException) exception).getConstraintViolations();
            if (CollectionUtils.isNotEmpty(sets)) {
                StringBuilder sb = new StringBuilder();
                sets.forEach(error -> {
                    if (error instanceof FieldError) {
                        sb.append(((FieldError) error).getField()).append(":");
                    }
                    sb.append(error.getMessage()).append(";");
                });
                String msg = sb.toString();
                msg = StringUtils.substring(msg, 0, msg.length() - 1);
                result.setMessage(msg);
            }
            return result;
        }

        if (exception instanceof BindException) {
            // get请求的对象参数校验异常
            Result result = Result.fail(BizCodeEnum.PARAM_ILLEGAL);
            List<ObjectError> errors = ((BindException) exception).getBindingResult().getAllErrors();
            String msg = getValidExceptionMsg(errors);
            if (StringUtils.isNotBlank(msg)) {
                result.setMessage(msg);
            }
            return result;
        }

        if (exception instanceof MethodArgumentNotValidException) {
            // post请求的对象参数校验异常
            Result result = Result.fail(BizCodeEnum.PARAM_ILLEGAL);
            List<ObjectError> errors = ((MethodArgumentNotValidException) exception).getBindingResult().getAllErrors();
            String msg = getValidExceptionMsg(errors);
            if (StringUtils.isNotBlank(msg)) {
                result.setMessage(msg);
            }
            return result;
        }

        if (exception instanceof BizException) {
            log.error("[业务异常] ===> ", exception);
            BizException bizException = (BizException) exception;
            return Result.build(bizException.getCode(), bizException.getMessage());
        }
        log.error("[未知异常] ===> ", exception);
        return Result.fail("未知异常");
    }

    private String getValidExceptionMsg(List<ObjectError> errors) {
        if (CollectionUtils.isNotEmpty(errors)) {
            StringBuilder sb = new StringBuilder();
            errors.forEach(error -> {
                if (error instanceof FieldError) {
                    sb.append(((FieldError) error).getField()).append(":");
                }
                sb.append(error.getDefaultMessage()).append(";");
            });
            String msg = sb.toString();
            msg = StringUtils.substring(msg, 0, msg.length() - 1);
            return msg;
        }
        return null;
    }

    @Override
    public boolean supports(@NotNull MethodParameter returnType,
                            @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  @NotNull MethodParameter returnType,
                                  @NotNull MediaType selectedContentType,
                                  @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NotNull ServerHttpRequest request,
                                  @NotNull ServerHttpResponse response) {
        response.getHeaders().set("Content-Type", "application/json;charset=utf-8");
        return body;
    }
}
