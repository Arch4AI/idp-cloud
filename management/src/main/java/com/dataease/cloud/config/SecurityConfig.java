package com.dataease.cloud.config;

import com.dataease.cloud.security.CustomSecurityConfigurerAdapter;
import com.dataease.cloud.security.exclude.DefaultSecurityExcludes;
import com.dataease.cloud.security.handler.CustomAccessDeniedHandler;
import com.dataease.cloud.security.handler.CustomAuthenticationEntryPoint;
import com.dataease.cloud.security.handler.CustomLogoutSuccessHandler;
import com.dataease.cloud.security.rememberme.RememberMeDetailsService;
import com.dataease.cloud.security.rememberme.service.CustomTokenBasedRememberMeServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;

/**
 * security 配置见下
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/6 1:55 PM
 **/
@Configuration
@Slf4j
public class SecurityConfig {

    private static final String REMEMBER_ME_KEY = "cloud_cloud";

    @Resource
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Resource
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Resource
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;

    @Resource
    private RememberMeDetailsService rememberMeDetailsService;

    @Resource
    private CustomSecurityConfigurerAdapter customSecurityConfigurerAdapter;


    /**
     * 定义过滤器
     * @param httpSecurity
     * @return
     * @throws Exception
     */
    @Bean
    @Order
    public SecurityFilterChain defaultSecurityWebFilterChain(HttpSecurity httpSecurity)throws Exception{
        return httpSecurity.authorizeHttpRequests().requestMatchers(DefaultSecurityExcludes.excludes)
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic()
                .disable()
                .formLogin()
                .disable()
                .csrf()
                .disable()
                .exceptionHandling()
                .accessDeniedHandler(customAccessDeniedHandler)
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .and()
                .logout()
                .logoutSuccessHandler(customLogoutSuccessHandler)
                .and()
                .sessionManagement()
                .maximumSessions(1)//暂时无效 没有实现redis-session的管理 不会踢出对应的session-key
                .and()
                .and()
                .rememberMe()
                .userDetailsService(rememberMeDetailsService)
                .rememberMeServices(rememberMeServices())
//                .alwaysRemember(true)
//                .key(REMEMBER_ME_KEY)
                .and()
                .apply(customSecurityConfigurerAdapter)
                .and()
                .build();
    }

    /**
     * 重写记住我
     * @return
     */
    @Bean
    public RememberMeServices rememberMeServices(){
        CustomTokenBasedRememberMeServices customTokenBasedRememberMeServices = new CustomTokenBasedRememberMeServices(REMEMBER_ME_KEY,rememberMeDetailsService,86400);
        customTokenBasedRememberMeServices.setAlwaysRemember(true);
        return customTokenBasedRememberMeServices;
    }
}
