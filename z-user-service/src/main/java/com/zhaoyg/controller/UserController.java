package com.zhaoyg.controller;

import com.zhaoyg.request.UserLoginRequest;
import com.zhaoyg.request.UserRegisterRequest;
import com.zhaoyg.service.UserService;
import com.zhaoyg.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zhao
 * @since 2022-08-11
 */
@Api(tags = "用户模块")
@RestController
@RequestMapping("/api/user/v1")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @ApiOperation("获取用户详情")
    @GetMapping("/detail")
    public Result detail() {
        return userService.detail();
    }

    @ApiOperation("上传头像")
    @PostMapping("/head")
    public Result upload(
            @ApiParam(name = "file", value = "头像文件") @RequestPart("file") MultipartFile file) {
        return userService.uploadImg(file);
    }

    @ApiOperation("注册用户")
    @PostMapping("/register")
    public Result register(@RequestBody UserRegisterRequest userRegisterRequest) {
        return userService.register(userRegisterRequest);
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result login(@RequestBody UserLoginRequest userLoginRequest,
                        HttpServletRequest httpServletRequest) {
        return userService.login(userLoginRequest, httpServletRequest);
    }

}

