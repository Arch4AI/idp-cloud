package com.dataease.cloud.security.external.filter;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.IoUtil;
import com.dataease.cloud.base.domain.CloudUser;
import com.dataease.cloud.constant.Constants;
import com.dataease.cloud.entity.response.Result;
import com.dataease.cloud.security.exception.SecurityException;
import com.dataease.cloud.security.external.ExternalAuthenticationToken;
import com.dataease.cloud.security.external.request.ExternalSecurityRequest;
import com.dataease.cloud.security.external.response.ExternalSecurityResponse;
import com.dataease.cloud.service.CloudExternalAuthSourceService;
import com.dataease.cloud.utils.ObjectMapperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fit2cloud.cloud.oauth.model.AuthUser;
import io.undertow.util.Headers;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 外部登录源
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/7 3:17 PM
 **/
@Slf4j
public class ExternalAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_FILTER_PROCESSES_URL = Constants.EXTERNAL_LOGIN_URI;

    private CloudExternalAuthSourceService cloudExternalAuthSourceService;

    public ExternalAuthenticationFilter() {
        super(new AntPathRequestMatcher(DEFAULT_FILTER_PROCESSES_URL, HttpMethod.POST.name()));
    }

    public ExternalAuthenticationFilter(AuthenticationManager authenticationManager, CloudExternalAuthSourceService cloudExternalAuthSourceService) {
        super(new AntPathRequestMatcher(DEFAULT_FILTER_PROCESSES_URL, HttpMethod.POST.name()),authenticationManager);
        this.cloudExternalAuthSourceService = cloudExternalAuthSourceService;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (!Objects.equals(request.getMethod(), HttpMethod.POST.name())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        // 后面可能还有编码 默认UTF-8
        if(!request.getHeader(Headers.CONTENT_TYPE_STRING).startsWith(MediaType.APPLICATION_JSON.toString())){
            SecurityException.throwException("External Auth Request Parameter error!");
        }
        ObjectMapper objectMapper = ObjectMapperUtils.simpleObjectMapper;
        //转码
        ExternalSecurityRequest externalSecurityRequest = null;
        try {
            //使用jackson去转对象
            String body = IoUtil.readUtf8(request.getInputStream());
            externalSecurityRequest = objectMapper.readValue(body, ExternalSecurityRequest.class);
        }catch (Exception e){
            // 参数错误
            SecurityException.throwException("Authentication parameter error!");
        }
        //在这里还要判断是否已经登录了
        if(externalSecurityRequest == null)
            SecurityException.throwException("Authentication parameter not supported!");
        //获取第三方用户
        AuthUser authUser = cloudExternalAuthSourceService.getAuthUser(externalSecurityRequest);
        //根据第三方用户是否已经绑定了用户本身
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //说明已经登录了开始绑定第三方用户
        if(authentication != null && !(authentication instanceof AnonymousAuthenticationToken)){
            //判断这个用户是不是已经被绑定了 绑定了就不能在被绑定
            //否则绑定这个用户
            try {
                cloudExternalAuthSourceService.bindUser(externalSecurityRequest.getState(),authUser);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.setContentType(MediaType.APPLICATION_JSON.toString());
                PrintWriter out = response.getWriter();
                out.write(objectMapper.writeValueAsString(Result.success("Bind user success!")));
                out.flush();
                out.close();
            }catch (Exception e){
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.setContentType(MediaType.APPLICATION_JSON.toString());
                PrintWriter out = response.getWriter();
                out.write(objectMapper.writeValueAsString(Result.error(e,"Bind user error!")));
                out.flush();
                out.close();
            }
            return null;
        }
        //下面就是登录的逻辑的 判断是否已经绑定了用户 绑定了用户直接返回用户直接登录
        CloudUser user = null;
        try {
            user = cloudExternalAuthSourceService.loginUser(authUser,externalSecurityRequest.getState());
        }catch (Exception e){
            // 如果是退出了也保存一下这个信息
            cloudExternalAuthSourceService.saveUser(authUser,externalSecurityRequest.getState());
            String sourceId = Base64.decodeStr(externalSecurityRequest.getState());
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON.toString());
            PrintWriter out = response.getWriter();
            out.write(objectMapper.writeValueAsString(Result.error(HttpStatus.FORBIDDEN,e,
                    ExternalSecurityResponse.builder()
                    .sourceId(sourceId)
                    .type(externalSecurityRequest.getType())
                    .uuid(authUser.getUuid())
                    .build())));
            out.flush();
            out.close();
            return null;
        }
        if(user == null)
            SecurityException.throwException("user not found!");
        if(!user.getEnabled())
            SecurityException.throwException("user has disabled!");
        //创建一个token 最好传入的是userId 否则直接在这里就报错出去
        ExternalAuthenticationToken authRequest = new ExternalAuthenticationToken(user.getUserId());
        // 身份验证详细信息
        authRequest.setDetails(super.authenticationDetailsSource.buildDetails(request));

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
