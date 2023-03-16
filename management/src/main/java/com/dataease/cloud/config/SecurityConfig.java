package com.dataease.cloud.config;

import com.dataease.cloud.security.CustomSecurityConfigurerAdapter;
import com.dataease.cloud.security.exclude.DefaultSecurityExcludes;
import com.dataease.cloud.security.handler.CustomAccessDeniedHandler;
import com.dataease.cloud.security.handler.CustomAuthenticationEntryPoint;
import com.dataease.cloud.security.handler.CustomLogoutSuccessHandler;
import com.dataease.cloud.security.rememberme.RememberMeDetailsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;

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
//                .sessionRegistry(sessionRegistry())
                .and()
                .and()
                .rememberMe()
                //.authenticationSuccessHandler(remembeMeSuccessHandler)
                .userDetailsService(rememberMeDetailsService)
                .alwaysRemember(true)
                .key(REMEMBER_ME_KEY)
                .and()
                .apply(customSecurityConfigurerAdapter)
                .and()
                .build();
    }
}
