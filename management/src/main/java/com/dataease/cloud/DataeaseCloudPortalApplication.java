package com.dataease.cloud;

import com.dataease.cloud.properties.IdpProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动器
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableMethodSecurity(prePostEnabled = true, securedEnabled=true)
public class DataeaseCloudPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataeaseCloudPortalApplication.class, args);
    }

}
