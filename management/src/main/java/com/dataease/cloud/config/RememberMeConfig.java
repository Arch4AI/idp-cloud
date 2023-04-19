package com.dataease.cloud.config;

import com.dataease.cloud.properties.SessionProperties;
import com.dataease.cloud.security.rememberme.RememberMeDetailsService;
import com.dataease.cloud.security.rememberme.service.PersistentTokenBasedRememberMeService;
import com.dataease.cloud.service.RememberMeTokenService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.RememberMeServices;

/**
 * rememberMe初始化
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/18 2:49 PM
 **/
@Configuration(proxyBeanMethods = false)
public class RememberMeConfig {

    @Resource
    private RememberMeDetailsService rememberMeDetailsService;

    @Resource
    private SessionProperties sessionProperties;

    @Resource
    private RememberMeTokenService rememberMeTokenService;


    @Bean
    public RememberMeServices rememberMeService() {
        PersistentTokenBasedRememberMeService rememberMeService = new PersistentTokenBasedRememberMeService(sessionProperties.getRememberTokenKey(),
                rememberMeDetailsService, rememberMeTokenService,
                sessionProperties.getSessionTimeOut());
        //总是记住我 不需要额外参数
        rememberMeService.setAlwaysRemember(sessionProperties.isAutoRememberMe());
        //记住我的cookie名称
        rememberMeService.setCookieName(sessionProperties.getRememberCookieName());
        //默认是两个星期
        rememberMeService.setTokenValiditySeconds(sessionProperties.getRememberMeTokenTime());
        return rememberMeService;
    }
}
