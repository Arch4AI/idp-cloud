package com.dataease.cloud.security.handler;

import com.dataease.cloud.base.domain.CloudUserLoginLog;
import com.dataease.cloud.base.mapper.CloudUserLoginLogMapper;
import com.dataease.cloud.entity.response.Result;
import com.dataease.cloud.security.LocalUser;
import com.dataease.cloud.service.SessionService;
import com.dataease.cloud.utils.CommonUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
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

    @Resource
    private SessionService sessionService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        Object principal = authentication.getPrincipal();
        jacksonObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 设置登录日志 这个地方已经是登录后触发的 登出的暂时没有 需要监听
        if (principal instanceof LocalUser) {
            //当用户登录成功后 将session的时间设置为1天
            request.getSession().setMaxInactiveInterval(86400);
            LocalUser localUser = (LocalUser) principal;
            localUser.setPassword(null);
            CloudUserLoginLog cloudUserLoginLog = new CloudUserLoginLog();
            cloudUserLoginLog.setLoginTime(Calendar.getInstance().getTime());
            cloudUserLoginLog.setUserId(localUser.getUserId());
            log.info("sessionId:{},IP:{}",request.getSession().getId(), CommonUtils.getIpAddress(request));
            cloudUserLoginLog.setSessionId(request.getSession().getId());
            cloudUserLoginLog.setIp(CommonUtils.getIpAddress(request));
            cloudUserLoginLogMapper.insertSelective(cloudUserLoginLog);
            // 添加信息
            sessionService.addOrUpdateSessionRecord(request.getSession(),localUser.getUserId());
        }

        //判断是不是有oauth2.0登录的请求 直接重定向？
        HttpSessionRequestCache cache = new HttpSessionRequestCache();
        SavedRequest savedRequest = cache.getRequest(request,response);
        // 测试这里理论上是可行的 但是开发环境因为跨域导致无法调试 暂时先不管这个？
        if(savedRequest != null){
            // 理论上目前只去支持oauth2.0的回调,其余的回调参数不支持咯
            if(savedRequest.getRedirectUrl().contains(authorizationServerSettings.getAuthorizationEndpoint())){
                String uri = ((DefaultSavedRequest)savedRequest).getRequestURL();
                StringBuffer buffer = new StringBuffer(uri).append("?").append(((DefaultSavedRequest) savedRequest).getQueryString());
                String redirectUri = buffer.toString();
                //返回的数据还是那样
                response.setStatus(HttpStatus.OK.value());
                response.setContentType(MediaType.APPLICATION_JSON.toString());
                PrintWriter out = response.getWriter();
                out.write(jacksonObjectMapper.writeValueAsString(Result.error(HttpStatus.FOUND,redirectUri,principal)));
                out.flush();
                out.close();
                return ;
            }
        }
        //默认返回200
        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        PrintWriter out = response.getWriter();
        out.write(jacksonObjectMapper.writeValueAsString(Result.success(principal)));
        out.flush();
        out.close();
    }
}
