package com.zhaoyg.config;

import com.zhaoyg.interceptor.AuthInterceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author zhao
 * @date 2022/8/13
 */
@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(new AuthInterceptor());
        registration.addPathPatterns(
                "/api/user/*/**",
                "/api/address/*/**");
        registration.excludePathPatterns(
                "/api/user/*/code",
                "/api/user/*/captcha",
                "/api/user/*/register",
                "/api/user/*/login",
                "/api/user/*/upload");
    }
}
