package com.dataease.cloud.security;

import com.dataease.cloud.security.external.ExternalAuthenticationProvider;
import com.dataease.cloud.security.external.filter.ExternalAuthenticationFilter;
import com.dataease.cloud.security.handler.CustomAuthenticationFailureHandler;
import com.dataease.cloud.security.handler.CustomSuccessHandler;
import com.dataease.cloud.security.password.PasswordAuthenticationProvider;
import com.dataease.cloud.security.password.filter.PasswordAuthenticationFilter;
import com.dataease.cloud.security.phone.PhoneAuthenticationProvider;
import com.dataease.cloud.security.phone.filter.PhoneAuthenticationFilter;
import com.dataease.cloud.security.profile.ProfileAuthenticationProvider;
import com.dataease.cloud.security.profile.filter.ProfileAuthenticationFilter;
import com.dataease.cloud.security.register.RegisterAuthenticationProvider;
import com.dataease.cloud.security.register.filter.RegisterAuthenticationFilter;
import com.dataease.cloud.service.SocialAuthenticationService;
import com.dataease.cloud.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;

/**
 * 自定义的安全管理器
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/14 10:04 AM
 **/
@Configuration(proxyBeanMethods = false)
public class CustomSecurityConfigurerAdapter extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Resource
    private LocalDetailsService localDetailsService;

    @Resource
    private CustomSuccessHandler customSuccessHandler;

    @Resource
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Resource
    private UserService userService;

    @Resource
    private SocialAuthenticationService socialAuthenticationService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        //获取密码的过滤器
        http.authenticationProvider(new PasswordAuthenticationProvider(localDetailsService))
                .addFilterAfter(getPasswordAuthenticationFilter(http), UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(new PhoneAuthenticationProvider(localDetailsService))
                .addFilterAfter(getPhoneAuthenticationFilter(http), UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(new RegisterAuthenticationProvider(localDetailsService))
                .addFilterAfter(getRegisterAuthenticationFilter(http), UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(new ProfileAuthenticationProvider(localDetailsService))
                .addFilterAfter(getProfileAuthenticationFilter(http), UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(new ExternalAuthenticationProvider(localDetailsService))
                .addFilterAfter(getExternalAuthenticationFilter(http), UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * 外部认证
     *
     * @param http
     * @return
     */
    private ExternalAuthenticationFilter getExternalAuthenticationFilter(HttpSecurity http) {
        SessionAuthenticationStrategy sessionAuthenticationStrategy = http
                .getSharedObject(SessionAuthenticationStrategy.class);
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        RememberMeServices rememberMeServices = http.getSharedObject(RememberMeServices.class);
        SecurityContextRepository repository = http.getSharedObject(SecurityContextRepository.class);

        ExternalAuthenticationFilter externalAuthenticationFilter = new ExternalAuthenticationFilter(authenticationManager, socialAuthenticationService);
        // 如果未设置这个会导致redis session中没有数据 需要将用户信息存储到httpSession中
        externalAuthenticationFilter.setSecurityContextRepository(repository);
        // 设置记住我
        externalAuthenticationFilter.setRememberMeServices(rememberMeServices);
        //session的
        externalAuthenticationFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        // 配置Filter执行完成后的处理器
        externalAuthenticationFilter.setAuthenticationSuccessHandler(customSuccessHandler);
        externalAuthenticationFilter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);
        return externalAuthenticationFilter;
    }

    /**
     * 获取更新信息的Filter
     *
     * @param http
     * @return
     */
    private ProfileAuthenticationFilter getProfileAuthenticationFilter(HttpSecurity http) {
        SessionAuthenticationStrategy sessionAuthenticationStrategy = http
                .getSharedObject(SessionAuthenticationStrategy.class);
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        RememberMeServices rememberMeServices = http.getSharedObject(RememberMeServices.class);
        SecurityContextRepository repository = http.getSharedObject(SecurityContextRepository.class);

        ProfileAuthenticationFilter profileAuthenticationFilter = new ProfileAuthenticationFilter(authenticationManager, userService, socialAuthenticationService);
        // 如果未设置这个会导致redis session中没有数据 需要将用户信息存储到httpSession中
        profileAuthenticationFilter.setSecurityContextRepository(repository);
        // 设置记住我
        profileAuthenticationFilter.setRememberMeServices(rememberMeServices);
        //session的
        profileAuthenticationFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        // 配置Filter执行完成后的处理器
        profileAuthenticationFilter.setAuthenticationSuccessHandler(customSuccessHandler);
        profileAuthenticationFilter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);
        profileAuthenticationFilter.setContextRepository(repository);
        return profileAuthenticationFilter;
    }

    /**
     * 获取注册过滤器
     *
     * @return
     */
    private RegisterAuthenticationFilter getRegisterAuthenticationFilter(HttpSecurity http) {
        SessionAuthenticationStrategy sessionAuthenticationStrategy = http
                .getSharedObject(SessionAuthenticationStrategy.class);
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        RememberMeServices rememberMeServices = http.getSharedObject(RememberMeServices.class);
        SecurityContextRepository repository = http.getSharedObject(SecurityContextRepository.class);
        // 配置一个Filter 默认使用已经存在的authenticationManager
        RegisterAuthenticationFilter registerAuthenticationFilter = new RegisterAuthenticationFilter(authenticationManager, userService, socialAuthenticationService);
        // 如果未设置这个会导致redis session中没有数据 需要将用户信息存储到httpSession中
        registerAuthenticationFilter.setSecurityContextRepository(repository);
        // 设置记住我
        registerAuthenticationFilter.setRememberMeServices(rememberMeServices);
        //session的
        registerAuthenticationFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        // 配置Filter执行完成后的处理器
        registerAuthenticationFilter.setAuthenticationSuccessHandler(customSuccessHandler);
        registerAuthenticationFilter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);
        return registerAuthenticationFilter;
    }

    /**
     * 获取手机登录的过滤器
     *
     * @param http
     * @return
     */
    private PhoneAuthenticationFilter getPhoneAuthenticationFilter(HttpSecurity http) {
        SessionAuthenticationStrategy sessionAuthenticationStrategy = http
                .getSharedObject(SessionAuthenticationStrategy.class);
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        RememberMeServices rememberMeServices = http.getSharedObject(RememberMeServices.class);
        SecurityContextRepository repository = http.getSharedObject(SecurityContextRepository.class);
        // 配置一个Filter 默认使用已经存在的authenticationManager
        PhoneAuthenticationFilter phoneAuthenticationFilter = new PhoneAuthenticationFilter(authenticationManager, stringRedisTemplate, userService, socialAuthenticationService);
        // 如果未设置这个会导致redis session中没有数据 需要将用户信息存储到httpSession中
        phoneAuthenticationFilter.setSecurityContextRepository(repository);
        // 设置记住我
        phoneAuthenticationFilter.setRememberMeServices(rememberMeServices);
        //session的
        phoneAuthenticationFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        // 配置Filter执行完成后的处理器
        phoneAuthenticationFilter.setAuthenticationSuccessHandler(customSuccessHandler);
        phoneAuthenticationFilter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);
        return phoneAuthenticationFilter;
    }

    /**
     * 密码登录的过滤器
     *
     * @param http
     * @return
     */
    private PasswordAuthenticationFilter getPasswordAuthenticationFilter(HttpSecurity http) {
        SessionAuthenticationStrategy sessionAuthenticationStrategy = http
                .getSharedObject(SessionAuthenticationStrategy.class);
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        RememberMeServices rememberMeServices = http.getSharedObject(RememberMeServices.class);
        SecurityContextRepository repository = http.getSharedObject(SecurityContextRepository.class);
        // 配置一个Filter 默认使用已经存在的authenticationManager
        PasswordAuthenticationFilter passwordAuthenticationFilter = new PasswordAuthenticationFilter(authenticationManager, userService, socialAuthenticationService);
        // 如果未设置这个会导致redis session中没有数据 需要将用户信息存储到httpSession中
        passwordAuthenticationFilter.setSecurityContextRepository(repository);
        //记住我
        passwordAuthenticationFilter.setRememberMeServices(rememberMeServices);
        passwordAuthenticationFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        // 配置Filter执行完成后的处理器
        passwordAuthenticationFilter.setAuthenticationSuccessHandler(customSuccessHandler);
        passwordAuthenticationFilter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);
        return passwordAuthenticationFilter;
    }
}
