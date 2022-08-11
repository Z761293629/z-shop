package com.zhaoyg.util;

import com.zhaoyg.enums.BizCodeEnum;
import lombok.Data;

/**
 * @author zhao
 * @date 2022/8/11
 */
@Data

public class Result {

    private Integer code;
    private Object data;
    private String message;


    public Result(Integer code) {
        this.code = code;
    }

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(Integer code, Object data) {
        this.code = code;
        this.data = data;
    }

    public Result(Integer code, Object data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }


    public static Result success() {
        return new Result(0, null, null);
    }

    public static Result success(Object data) {
        return new Result(0, data);
    }

    public static Result build(Integer code, String message) {
        return new Result(code, message);
    }

    public static Result fail(BizCodeEnum codeEnum) {
        return new Result(codeEnum.getCode(), codeEnum.getMessage());
    }

    public static Result fail(String message) {
        return new Result(-1, message);
    }

}
