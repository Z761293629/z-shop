package com.zhaoyg.interceptor;

import com.zhaoyg.entity.LoginUser;
import com.zhaoyg.enums.BizCodeEnum;
import com.zhaoyg.util.CommonUtil;
import com.zhaoyg.util.JwtUtil;
import com.zhaoyg.util.Result;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author zhao
 * @date 2022/8/13
 */
public class AuthInterceptor extends HandlerInterceptorAdapter {

    public static final ThreadLocal<LoginUser> loginUserThreadLocal = new ThreadLocal<>();
    public static final ThreadLocal<String> token = new ThreadLocal<>();

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) throws Exception {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotBlank(authorization)) {
            LoginUser loginUser = JwtUtil.parseJwt(authorization, request);
            if (Objects.nonNull(loginUser)) {
                token.set(authorization);
                loginUserThreadLocal.set(loginUser);
                return true;
            }
        }
        CommonUtil.sendResponse(response, Result.fail(BizCodeEnum.ACCOUNT_UNAUTHORIZED));
        return false;
    }
}
