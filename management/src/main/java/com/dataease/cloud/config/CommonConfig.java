package com.dataease.cloud.config;

import com.dataease.cloud.base.mapper.CloudUserMapper;
import com.dataease.cloud.base.mapper.CloudUserRoleMapper;
import com.dataease.cloud.init.SystemInit;
import com.dataease.cloud.service.CloudExternalAuthSourceService;
import com.dataease.cloud.utils.CommonBeanFactory;
import com.dataease.cloud.utils.GlobalConfigurations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * 通用的配置服务
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/7 2:12 PM
 **/
@Configuration
@PropertySource("classpath:properties/global.properties")
public class CommonConfig {

    @Bean
    public SystemInit systemInit(CloudUserMapper cloudUserMapper,
                                 CloudUserRoleMapper cloudUserRoleMapper,
                                 CloudExternalAuthSourceService cloudOauthProviderSettingService) {
        return new SystemInit(cloudUserMapper, cloudUserRoleMapper, cloudOauthProviderSettingService);
    }

    @Bean
    public CommonBeanFactory commonBeanFactory() {
        return new CommonBeanFactory();
    }

    @Bean
    public GlobalConfigurations globalConfigurations(Environment environment) {
        return new GlobalConfigurations(environment);
    }
}
