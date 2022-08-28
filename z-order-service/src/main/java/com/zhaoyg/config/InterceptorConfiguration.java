package com.zhaoyg.config;

import cn.hutool.core.util.StrUtil;
import com.zhaoyg.interceptor.AuthInterceptor;
import feign.RequestInterceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;

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
                "/api/order/*/**"
        );
        registration.excludePathPatterns(
                "/api/order/*/state",
                "/api/order/*//test_pay",
                "/api/callback/*/**"
        );
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes == null) {
                return;
            }
            HttpServletRequest request = requestAttributes.getRequest();
            String authorization = request.getHeader("Authorization");
            if (StrUtil.isBlank(authorization)) {
                return;
            }
            requestTemplate.header("Authorization", authorization);
        };
    }
}
