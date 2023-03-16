package com.dataease.cloud.security.handler;

import com.dataease.cloud.base.domain.CloudUserLoginLog;
import com.dataease.cloud.base.mapper.CloudUserLoginLogMapper;
import com.dataease.cloud.security.LocalUser;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

/**
 * 登录成功处理Handler
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/7 5:48 PM
 **/
@Component
@Slf4j
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Resource
    private CloudUserLoginLogMapper cloudUserLoginLogMapper;

    @Resource
    private ObjectMapper jacksonObjectMapper;

    @Resource
    private AuthorizationServerSettings authorizationServerSettings;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Object principal = authentication.getPrincipal();
        // 设置登录日志 这个地方已经是登录后触发的 登出的暂时没有 需要监听
        if (principal instanceof LocalUser) {
            //当用户登录成功后 将session的时间设置为1天
            request.getSession().setMaxInactiveInterval(86400);
            LocalUser localUser = (LocalUser) principal;
            localUser.setPassword(null);
            CloudUserLoginLog cloudUserLoginLog = new CloudUserLoginLog();
            cloudUserLoginLog.setLoginTime(Calendar.getInstance().getTime());
            cloudUserLoginLog.setUserId(localUser.getUserId());
            log.info("sessionId:{},IP:{}",request.getSession().getId(),request.getRemoteAddr());
            cloudUserLoginLog.setSessionId(request.getSession().getId());
            cloudUserLoginLog.setIp(request.getRemoteAddr());
            cloudUserLoginLogMapper.insertSelective(cloudUserLoginLog);
            log.info("用户{}登录成功了",localUser.getUsername());
        }

        //判断是不是有oauth2.0登录的请求 直接重定向？
        HttpSessionRequestCache cache = new HttpSessionRequestCache();
        SavedRequest savedRequest = cache.getRequest(request,response);
        if(savedRequest != null){
            RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
            // 仅支持对oauth 2.0的进行回调
            if(savedRequest.getRedirectUrl().contains(authorizationServerSettings.getAuthorizationEndpoint())){
                // 需要重定向到前端登录页面哦
                redirectStrategy.sendRedirect(request,response,savedRequest.getRedirectUrl());
                return ;
            }
        }

        //不返回NULL
        jacksonObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        PrintWriter out = response.getWriter();
        out.write(jacksonObjectMapper.writeValueAsString(principal));
        out.flush();
        out.close();
    }
}
