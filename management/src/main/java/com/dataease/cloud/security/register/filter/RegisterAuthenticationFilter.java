package com.dataease.cloud.security.register.filter;

import cn.hutool.core.io.IoUtil;
import com.dataease.cloud.constant.Constants;
import com.dataease.cloud.i18n.I18nService;
import com.dataease.cloud.properties.CommonProperties;
import com.dataease.cloud.security.LocalUser;
import com.dataease.cloud.security.exception.SecurityException;
import com.dataease.cloud.security.register.RegisterAuthenticationToken;
import com.dataease.cloud.security.register.request.RegisterRequest;
import com.dataease.cloud.service.ExternalAuthSourceService;
import com.dataease.cloud.service.UserService;
import com.dataease.cloud.utils.EncryptUtils;
import com.dataease.cloud.utils.ObjectMapperUtils;
import io.undertow.util.Headers;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * 用户注册的过滤器
 * 需要注意的是这个地方手机号码的验证已经验证通过了才可以注册
 * 注册只管基本信息 不管手机验证码 如果是需要手机验证码的去调用/sms/check进行校验
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/7 3:17 PM
 **/
@Slf4j
public class RegisterAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_FILTER_PROCESSES_URL = Constants.REGISTER_URI;

    private UserService userService;

    private ExternalAuthSourceService externalAuthSourceService;

    public RegisterAuthenticationFilter() {
        super(new AntPathRequestMatcher(DEFAULT_FILTER_PROCESSES_URL, HttpMethod.POST.name()));
    }

    public RegisterAuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, ExternalAuthSourceService externalAuthSourceService) {
        super(new AntPathRequestMatcher(DEFAULT_FILTER_PROCESSES_URL, HttpMethod.POST.name()), authenticationManager);
        this.userService = userService;
        this.externalAuthSourceService = externalAuthSourceService;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (!Objects.equals(request.getMethod(), HttpMethod.POST.name())) {
            SecurityException.throwException(I18nService.getMessage("auth_method_not_support",request.getMethod()));
        }
        // 后面可能还有编码 默认UTF-8
        if (!request.getHeader(Headers.CONTENT_TYPE_STRING).startsWith(MediaType.APPLICATION_JSON.toString())) {
            SecurityException.throwException(I18nService.getMessage("request_param_error"));
        }
        RegisterRequest req = null;
        //转码
        try {
            String body = IoUtil.readUtf8(request.getInputStream());
            //如果开启了加密的话 需要在这里先解密
            if (CommonProperties.isEnableRequestBodyEncrypt()) {
                body = String.valueOf(EncryptUtils.aesDecrypt(body));
            }
            req = ObjectMapperUtils.simpleObjectMapper.readValue(body, RegisterRequest.class);
        } catch (Exception e) {
            // 参数错误
            SecurityException.throwException(I18nService.getMessage("request_param_error"));
        }
        LocalUser user = null;
        try {
            user = userService.registerUser(req);
        } catch (Exception e) {
            SecurityException.throwException(e);
        }
        //如果是传递进来了 就绑定一下第三方的 这个绑定的意思就是这个已经存在才可以绑定
        if (StringUtils.hasText(req.getSourceId()) && StringUtils.hasText(req.getUuid())) {
            externalAuthSourceService.bindUserRelation(user.getUserId(), req.getSourceId(), req.getUuid());
        }
        //创建一个token 传入获取到的用户request
        RegisterAuthenticationToken authRequest = new RegisterAuthenticationToken(user.getUserId());
        // 身份验证详细信息
        authRequest.setDetails(super.authenticationDetailsSource.buildDetails(request));

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
