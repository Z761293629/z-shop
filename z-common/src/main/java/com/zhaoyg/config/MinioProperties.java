package com.zhaoyg.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhao
 * @date 2022/8/12
 */
@Data
@ConfigurationProperties(prefix = "minio")
class MinioProperties {

    private String endpoint;
    private String accessKey;
    private String secretKey;

}
