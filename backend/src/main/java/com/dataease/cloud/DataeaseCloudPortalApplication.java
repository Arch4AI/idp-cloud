package com.dataease.cloud;

import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * 启动器
 * 默认去掉quartz自动初始化 采用自己的{@link com.dataease.cloud.quartz.config.QuartzAutoConfiguration}
 */
@SpringBootApplication(exclude = {
        QuartzAutoConfiguration.class,
        MybatisAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class
})
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class DataeaseCloudPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataeaseCloudPortalApplication.class, args);
    }

}
