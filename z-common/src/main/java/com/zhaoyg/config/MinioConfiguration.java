package com.zhaoyg.config;

import io.minio.MinioClient;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhao
 * @date 2022/8/12
 */
@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfiguration {

    @Bean
    public MinioClient minioClient(MinioProperties minioProperties) {
        return MinioClient.builder()
                .httpClient(new OkHttpClient())
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }


}
