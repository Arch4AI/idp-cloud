package com.dataease.cloud.security.password.filter;

import cn.hutool.core.io.IoUtil;
import com.dataease.cloud.constant.Constants;
import com.dataease.cloud.properties.CommonProperties;
import com.dataease.cloud.security.LocalUser;
import com.dataease.cloud.security.exception.SecurityException;
import com.dataease.cloud.security.password.PasswordAuthenticationToken;
import com.dataease.cloud.security.password.request.PasswordLoginRequest;
import com.dataease.cloud.service.ExternalAuthSourceService;
import com.dataease.cloud.service.UserService;
import com.dataease.cloud.utils.EncryptUtils;
import com.dataease.cloud.utils.ObjectMapperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.util.Headers;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * 密码登录的验证器
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/7 3:17 PM
 **/
@Slf4j
public class PasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_FILTER_PROCESSES_URL = Constants.PASSWORD_LOGIN_URI;

    private UserService userService;

    private ExternalAuthSourceService externalAuthSourceService;

    public PasswordAuthenticationFilter() {
        super(new AntPathRequestMatcher(DEFAULT_FILTER_PROCESSES_URL, HttpMethod.POST.name()));
    }

    public PasswordAuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, ExternalAuthSourceService externalAuthSourceService) {
        super(new AntPathRequestMatcher(DEFAULT_FILTER_PROCESSES_URL, HttpMethod.POST.name()), authenticationManager);
        this.userService = userService;
        this.externalAuthSourceService = externalAuthSourceService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (!Objects.equals(request.getMethod(), HttpMethod.POST.name())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        // 后面可能还有编码 默认UTF-8
        if (!request.getHeader(Headers.CONTENT_TYPE_STRING).startsWith(MediaType.APPLICATION_JSON.toString())) {
            SecurityException.throwException("Password Auth Request Parameter error!");
        }
        ObjectMapper objectMapper = ObjectMapperUtils.simpleObjectMapper;
        //转码
        PasswordLoginRequest passwordLoginRequest = null;
        try {
            //使用jackson去转对象
            String body = IoUtil.readUtf8(request.getInputStream());
            //如果开启了加密的话 需要在这里先解密
            if (CommonProperties.isEnableRequestBodyEncrypt()) {
                body = String.valueOf(EncryptUtils.aesDecrypt(body));
            }
            passwordLoginRequest = objectMapper.readValue(body, PasswordLoginRequest.class);
        } catch (Exception e) {
            // 参数错误
            SecurityException.throwException("Authentication parameter error!");
        }
        if (!StringUtils.hasText(passwordLoginRequest.getUserName()))
            throw new UsernameNotFoundException("userName/phone/email is Required!");
        if (!StringUtils.hasText(passwordLoginRequest.getPassword()))
            throw new BadCredentialsException("Password is Required!");
        //查询用户信息
        LocalUser user = userService.getUserByName(passwordLoginRequest.getUserName());
        //判断用户是否存在
        if (user == null)
            throw new UsernameNotFoundException("user not found!");
        if (!user.isEnabled())
            throw new DisabledException("user disabled!");
        //如果是传递进来了 就绑定一下第三方的 这个绑定的意思就是这个已经存在才可以绑定
        if (StringUtils.hasText(passwordLoginRequest.getSourceId()) && StringUtils.hasText(passwordLoginRequest.getUuid())) {
            externalAuthSourceService.bindUserRelation(user.getUserId(), passwordLoginRequest.getSourceId(), passwordLoginRequest.getUuid());
        }
        if (user != null) {
            request.setAttribute(Constants.REQ_USER_ATTRIBUTE, user);
        }
        //创建一个token
        PasswordAuthenticationToken authRequest = new PasswordAuthenticationToken(user.getUserId(), passwordLoginRequest.getPassword());
        // 身份验证详细信息
        authRequest.setDetails(super.authenticationDetailsSource.buildDetails(request));

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
