package com.zhaoyg.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spi.service.contexts.SecurityContextBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

/**
 * @author zhao
 * @date 2022/8/11
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket apiRestApi() {

        return new Docket(DocumentationType.OAS_30)
                .groupName("用户接口文档")
                .pathMapping("/")
                .enable(true)
                .apiInfo(apiInfo())
                // https://blog.csdn.net/z28126308/article/details/112315224
                // Authorization 请求头不生效
                .securityContexts(securityContexts())
                .securitySchemes(List.of(new ApiKey("Authorization", "Authorization", "header")))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.zhaoyg"))
                .paths(PathSelectors.ant("/api/**"))
                .build()
                //.globalRequestParameters(globalRequestParameters())
                ;
    }

    @NotNull
    private static List<SecurityContext> securityContexts() {
        List<SecurityReference> securityReferences = List.of(
                SecurityReference.builder()
                        .reference("Authorization")
                        .scopes(new AuthorizationScope[]{new AuthorizationScope("global", "accessEverything")})
                        .build()
        );
        return List.of(
                new SecurityContextBuilder()
                        .securityReferences(securityReferences)
                        .build()
        );
    }

    private List<RequestParameter> globalRequestParameters() {

        RequestParameter jwt = new RequestParameterBuilder()
                .description("登录令牌")
                .name(HttpHeaders.AUTHORIZATION)
                .in(ParameterType.HEADER)
                .query(simpleParameter -> simpleParameter.model(model -> model.scalarModel(ScalarType.STRING)))
                .required(false)
                .build();
        return List.of(jwt);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .version("v1")
                .title("电商平台")
                .description("微服务接口文档")
                .contact(new Contact("zhao", "w@w", "zhao@gmail.com"))
                .build();
    }
}
