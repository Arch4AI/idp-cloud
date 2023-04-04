package com.dataease.cloud.security.handler;

import com.dataease.cloud.entity.response.Result;
import com.dataease.cloud.properties.IdpProperties;
import com.dataease.cloud.utils.GlobalConfigurations;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 没有验证默认返回这个 其实应该加一些其他的参数进来
 * 默认返回403
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/7 9:05 PM
 **/
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Resource
    private ObjectMapper jacksonObjectMapper;

    @Resource
    private AuthorizationServerSettings authorizationServerSettings;

    @Resource
    private IdpProperties idpProperties;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        //这里应该还需要判断 是不是从oauth2.0访问过来的 如果是没有验证应该重定向到vue前端的登录界面去
        HttpSessionRequestCache cache = new HttpSessionRequestCache();
        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        SavedRequest savedRequest = cache.getRequest(request,response);
        if(savedRequest != null && savedRequest.getRedirectUrl().contains(authorizationServerSettings.getAuthorizationEndpoint())){
            // 需要重定向到前端登录页面哦
            // 需要重定向到登录页面去
            if(GlobalConfigurations.isReleaseMode()){
                redirectStrategy.sendRedirect(request,response,"/login");
            }else{
                redirectStrategy.sendRedirect(request,response, idpProperties.getViteAddr()+"/login");
            }

        }else{
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON.toString());
            response.setStatus(HttpStatus.FORBIDDEN.value());
            PrintWriter out = response.getWriter();
            out.write(jacksonObjectMapper.writeValueAsString(Result.error(HttpStatus.FORBIDDEN,authException,"用户未登录无法访问！")));
            out.flush();
            out.close();
        }
    }
}
