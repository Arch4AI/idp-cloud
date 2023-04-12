package com.dataease.cloud.config;

import com.dataease.cloud.properties.CommonProperties;
import com.dataease.cloud.security.CustomSecurityConfigurerAdapter;
import com.dataease.cloud.security.exclude.DefaultSecurityExcludes;
import com.dataease.cloud.security.handler.CustomAccessDeniedHandler;
import com.dataease.cloud.security.handler.CustomAuthenticationEntryPoint;
import com.dataease.cloud.security.handler.CustomLogoutSuccessHandler;
import com.dataease.cloud.security.rememberme.service.PersistentTokenBasedRememberMeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * security 配置见下
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/6 1:55 PM
 **/
@Configuration(proxyBeanMethods = false)
@Slf4j
public class SecurityConfig {

    /**
     * 定义过滤器
     *
     * @param httpSecurity
     * @return
     * @throws Exception
     */
    @Bean
    @Order
    public SecurityFilterChain defaultSecurityWebFilterChain(HttpSecurity httpSecurity,
                                                             SessionRegistry sessionRegistry,
                                                             CommonProperties commonProperties,
                                                             CustomAccessDeniedHandler customAccessDeniedHandler,
                                                             CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                                                             CustomLogoutSuccessHandler customLogoutSuccessHandler,
                                                             PersistentTokenBasedRememberMeService rememberMeService,
                                                             CustomSecurityConfigurerAdapter customSecurityConfigurerAdapter) throws Exception {
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
                //负一代表无数次登录
                .maximumSessions(commonProperties.getMaximumSessions())
                .sessionRegistry(sessionRegistry)
                .and()
                .and()
                .rememberMe()
                .rememberMeServices(rememberMeService)
                .and()
                .apply(customSecurityConfigurerAdapter)
                .and()
                .build();
    }

    /**
     * 配置cors跨域访问
     *
     * @return
     */
    @Bean
    public CorsFilter corsFilter() {
        //1.添加CORS配置信息
        CorsConfiguration config = new CorsConfiguration();
        //放行哪些原始域
        config.addAllowedOriginPattern("*");
        //是否发送Cookie信息
        config.setAllowCredentials(true);
        //放行哪些原始域(请求方式)
        config.addAllowedMethod("*");
        //放行哪些原始域(头部信息)
        config.addAllowedHeader("*");
        //暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）
        config.addExposedHeader("*");
        //2.添加映射路径
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);
        //3.返回新的CorsFilter.
        return new CorsFilter(configSource);
    }
}
