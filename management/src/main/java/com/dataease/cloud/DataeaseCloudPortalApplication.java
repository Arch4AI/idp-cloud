package com.dataease.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动器
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableAsync
public class DataeaseCloudPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataeaseCloudPortalApplication.class, args);
    }

}
