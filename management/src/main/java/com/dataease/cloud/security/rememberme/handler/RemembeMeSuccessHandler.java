package com.dataease.cloud.security.rememberme.handler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 记住我,成功验证后触发的Handler
 * @Deprecated 暂时未用到 后续再说 目前无法通过这个去修改session的超时时间
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/10 5:55 PM
 **/
@Component
@Slf4j
@Deprecated
public class RemembeMeSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        log.info("*******************remeber me authentication success****************");
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //设置httpsession的超时时间 1天
        log.info("*******************remeber me authentication success****************");
        onAuthenticationSuccess(request,response,authentication);
    }
}
