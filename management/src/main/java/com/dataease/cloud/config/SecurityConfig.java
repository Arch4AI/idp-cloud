package com.dataease.cloud.config;

import com.dataease.cloud.properties.SessionProperties;
import com.dataease.cloud.security.CustomSecurityConfigurerAdapter;
import com.dataease.cloud.security.exclude.DefaultSecurityExcludes;
import com.dataease.cloud.security.handler.*;
import com.dataease.cloud.security.social.configurer.SocialLoginConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
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
@ConditionalOnBean({SessionProperties.class})
public class SecurityConfig {

    /**
     * 定义过滤器
     *
     * @param httpSecurity
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain defaultSecurityWebFilterChain(HttpSecurity httpSecurity,
                                                             SessionRegistry sessionRegistry,
                                                             SessionProperties sessionProperties,
                                                             CustomAccessDeniedHandler customAccessDeniedHandler,
                                                             CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                                                             CustomLogoutSuccessHandler customLogoutSuccessHandler,
                                                             RememberMeServices rememberMeServices,
                                                             SocialLoginConfigurer socialLoginConfigurer,
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
                .maximumSessions(sessionProperties.getMaximumSessions())
                .maxSessionsPreventsLogin(sessionProperties.isMaxSessionsPreventsLogin())
                .sessionRegistry(sessionRegistry)
                .and()
                .and()
                .rememberMe()
                .rememberMeServices(rememberMeServices)
                .and()
                .apply(customSecurityConfigurerAdapter)
                .and()
                .apply(socialLoginConfigurer)
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
