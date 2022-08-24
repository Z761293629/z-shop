package com.zhaoyg.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.zhaoyg.component.MailService;
import com.zhaoyg.constant.CacheKey;
import com.zhaoyg.enums.BizCodeEnum;
import com.zhaoyg.enums.SendCodeEnum;
import com.zhaoyg.service.NotifyService;
import com.zhaoyg.util.CheckUtil;
import com.zhaoyg.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author zhao
 * @date 2022/8/12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotifyServiceImpl implements NotifyService {

    private final RedisTemplate<String, String> redisTemplate;
    private final MailService mailService;

    private static final long CODE_EXPIRE_MINUTE = 10L;
    /**
     * 10s
     */
    private static final long CODE_LIMIT = 60 * 1000L;
    private static final String REGISTER_CONTENT = "欢迎注册，验证码为%s，有效期为 : " + CODE_EXPIRE_MINUTE + " 分钟。";

    @Override
    public Result sendCode(SendCodeEnum sendCodeEnum, String to) {
        String cacheKey = CacheKey.codeKey(sendCodeEnum.name(), to);
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);

        boolean limit = Objects.nonNull(cacheValue) &&
                Long.parseLong(cacheValue.substring(cacheValue.indexOf("_") + 1)) + CODE_LIMIT > System.currentTimeMillis();
        if (limit) {
            return Result.fail(BizCodeEnum.CODE_LIMITED);
        }
        String code = RandomUtil.randomString(6);
        if (CheckUtil.isEmail(to)) {
            mailService.sendSimpleMail(to, "测试", String.format(REGISTER_CONTENT, code));
            redisTemplate.opsForValue().set(cacheKey, code + "_" + System.currentTimeMillis(), CODE_EXPIRE_MINUTE, TimeUnit.MINUTES);
            return Result.success();
        } else if (CheckUtil.isPhone(to)) {
            //TODO
        }
        return Result.fail(BizCodeEnum.CODE_TO_ERROR);
    }

    @Override
    public boolean checkCode(SendCodeEnum sendCodeEnum, String code, String to) {
        String cacheKey = CacheKey.codeKey(sendCodeEnum.name(), to);
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);

        return Objects.nonNull(cacheValue) &&
                Objects.equals(cacheValue.substring(0, cacheValue.indexOf("_")), code);
    }
}
