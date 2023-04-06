package com.dataease.cloud.security.profile.filter;

import cn.hutool.core.io.IoUtil;
import com.dataease.cloud.constant.Constants;
import com.dataease.cloud.entity.response.Result;
import com.dataease.cloud.security.LocalUser;
import com.dataease.cloud.security.exception.SecurityException;
import com.dataease.cloud.security.profile.ProfileAuthenticationToken;
import com.dataease.cloud.security.profile.request.UserUpdateProfileRequest;
import com.dataease.cloud.service.CloudExternalAuthSourceService;
import com.dataease.cloud.service.CloudUserService;
import com.dataease.cloud.utils.ObjectMapperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.util.Headers;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 修改用户信息
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/7 3:17 PM
 **/
@Slf4j
public class ProfileAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_FILTER_PROCESSES_URL = Constants.USER_PROFILE;

    private ObjectMapper objectMapper;

    private CloudUserService cloudUserService;

    private CloudExternalAuthSourceService cloudExternalAuthSourceService;
    private SecurityContextRepository contextRepository;

    public ProfileAuthenticationFilter() {
        super(new AntPathRequestMatcher(DEFAULT_FILTER_PROCESSES_URL, HttpMethod.POST.name()));
    }

    public ProfileAuthenticationFilter(AuthenticationManager authenticationManager, CloudUserService cloudUserService, CloudExternalAuthSourceService cloudExternalAuthSourceService) {
        super(new AntPathRequestMatcher(DEFAULT_FILTER_PROCESSES_URL, HttpMethod.POST.name()), authenticationManager);
        this.cloudUserService = cloudUserService;
        this.cloudExternalAuthSourceService = cloudExternalAuthSourceService;
        this.objectMapper = ObjectMapperUtils.simpleObjectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (!Objects.equals(request.getMethod(), HttpMethod.POST.name())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        // 后面可能还有编码 默认UTF-8
        if (!request.getHeader(Headers.CONTENT_TYPE_STRING).startsWith(MediaType.APPLICATION_JSON.toString())) {
            SecurityException.throwException("update user profile paramter error!");
        }
        UserUpdateProfileRequest req = null;
        //转码
        try {
            String body = IoUtil.readUtf8(request.getInputStream());
            req = this.objectMapper.readValue(body, UserUpdateProfileRequest.class);
        } catch (Exception e) {
            // 参数错误
            SecurityException.throwException("update user profile paramter error!");
        }
        LocalUser user = null;
        //已经验证了
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            log.info("Session:{},User has Login,Only User Profile.", request.getSession().getId());
            LocalUser localUser = (LocalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            req.setUserId(localUser.getUserId());
            try {
                LocalUser u = cloudUserService.updateProfile(req);
                //更新当前用户的信息 稍等一会在执行
                SecurityContext context = SecurityContextHolder.getContext();
                ProfileAuthenticationToken token = new ProfileAuthenticationToken(localUser, localUser.getAuthorities());
                token.setDetails(authentication.getDetails());
                context.setAuthentication(token);
                //这个配置试试先
                this.contextRepository.saveContext(context, request, response);
                //直接返回用户信息即可
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.setContentType(MediaType.APPLICATION_JSON.toString());
                PrintWriter out = response.getWriter();
                u.setPassword(null);
                out.write(objectMapper.writeValueAsString(Result.success(u)));
                out.flush();
                out.close();
            } catch (Exception e) {
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.setContentType(MediaType.APPLICATION_JSON.toString());
                PrintWriter out = response.getWriter();
                out.write(objectMapper.writeValueAsString(Result.error(e)));
                out.flush();
                out.close();
            }
            return null;
        }
        //这里相当于是注册
        user = cloudUserService.registerUserByProfile(req);
        //如果是传递进来了 就绑定一下第三方的 这个绑定的意思就是这个已经存在才可以绑定
        if (StringUtils.hasText(req.getSourceId()) && StringUtils.hasText(req.getUuid())) {
            cloudExternalAuthSourceService.bindUserRelation(user.getUserId(), req.getSourceId(), req.getUuid());
        }

        //创建一个token 传入获取到的用户request
        ProfileAuthenticationToken authRequest = new ProfileAuthenticationToken(user.getUserId());
        // 身份验证详细信息
        authRequest.setDetails(super.authenticationDetailsSource.buildDetails(request));

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    public void setContextRepository(SecurityContextRepository contextRepository) {
        this.contextRepository = contextRepository;
    }
}
