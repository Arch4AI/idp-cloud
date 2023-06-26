package com.dataease.cloud.config;

import com.dataease.cloud.base.mapper.CloudUserMapper;
import com.dataease.cloud.base.mapper.CloudUserRoleMapper;
import com.dataease.cloud.i18n.I18nService;
import com.dataease.cloud.init.SystemInit;
import com.dataease.cloud.utils.CommonBeanFactory;
import com.dataease.cloud.utils.GlobalConfigurations;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 通用的配置服务
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/7 2:12 PM
 **/
@Configuration(proxyBeanMethods = false)
@PropertySource("classpath:properties/global.properties")
@EnableAsync
public class CommonConfig {

    /**
     * 系统初始化
     * 后续需要去掉
     *
     * @param cloudUserMapper
     * @param cloudUserRoleMapper
     * @return
     */
    @Bean
    public SystemInit systemInit(CloudUserMapper cloudUserMapper,
                                 CloudUserRoleMapper cloudUserRoleMapper) {
        return new SystemInit(cloudUserMapper, cloudUserRoleMapper);
    }

    @Bean
    public CommonBeanFactory commonBeanFactory() {
        return new CommonBeanFactory();
    }

    @Bean
    public GlobalConfigurations globalConfigurations(Environment environment) {
        return new GlobalConfigurations(environment);
    }

    /**
     * 国际化的注入
     *
     * @param messageSource
     * @return
     */
    @Bean
    public I18nService i18nService(MessageSource messageSource) {
        return new I18nService(messageSource);
    }

}
