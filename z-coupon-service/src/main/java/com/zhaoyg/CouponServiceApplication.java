package com.zhaoyg;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author zhao
 * @date 2022/8/14
 */
@SpringBootApplication
@MapperScan("com.zhaoyg.mapper")
@EnableTransactionManagement
@EnableFeignClients
public class CouponServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponServiceApplication.class);
    }
}
