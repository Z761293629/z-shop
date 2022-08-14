package com.zhaoyg.controller;

import cn.hutool.crypto.SecureUtil;
import com.google.code.kaptcha.Producer;
import com.zhaoyg.enums.BizCodeEnum;
import com.zhaoyg.enums.SendCodeEnum;
import com.zhaoyg.service.NotifyService;
import com.zhaoyg.util.CommonUtil;
import com.zhaoyg.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * @author zhao
 * @date 2022/8/11
 */
@Api(tags = "通知模块")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/v1")
public class NotifyController {

    private final Producer captchaProducer;
    private final RedisTemplate<String, String> redisTemplate;

    private final NotifyService notifyService;

    /**
     * 验证码失效时间 3 minute
     */
    private static final long CAPTCHA_EXPIRE = 3 * 60 * 1000L;

    /**
     * 获取图形验证码
     *
     * @param request  请求
     * @param response 响应
     */
    @ApiOperation("获取图形验证码")
    @GetMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) {
        String text = captchaProducer.createText();
        BufferedImage bufferedImage = captchaProducer.createImage(text);
        redisTemplate.opsForValue().set(captchaKey(request), text, CAPTCHA_EXPIRE, TimeUnit.MILLISECONDS);
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            ImageIO.write(bufferedImage, "jpg", outputStream);
            outputStream.flush();
        } catch (IOException exception) {
            log.error("获取图形验证码失败", exception);
        }
    }

    @ApiOperation("获取验证码")
    @GetMapping("/code")
    public Result sendCode(HttpServletRequest request,
                           @ApiParam(name = "邮箱或手机", required = true) String to,
                           @ApiParam(name = "图形验证码", required = true) String captcha) {

        String cacheKey = captchaKey(request);
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);
        if (Objects.nonNull(cacheValue) && cacheValue.equalsIgnoreCase(captcha)) {
            redisTemplate.delete(cacheKey);
            return notifyService.sendCode(SendCodeEnum.USER_REGISTER, to);
        } else {
            return Result.fail(BizCodeEnum.CODE_CAPTCHA);
        }
    }


    private String captchaKey(HttpServletRequest request) {
        String ipAddr = CommonUtil.getIpAddr(request);
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        String key = "user-service:captcha:" + SecureUtil.md5().digestHex(ipAddr + userAgent);
        log.info("ipAddr : [{}]", ipAddr);
        log.info("userAgent : [{}]", userAgent);
        log.info("key : [{}]", key);
        return key;
    }
}
