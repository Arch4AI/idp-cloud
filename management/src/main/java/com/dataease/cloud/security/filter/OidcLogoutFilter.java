package com.dataease.cloud.security.filter;

import com.dataease.cloud.base.domain.CloudOauth2Authorization;
import com.dataease.cloud.base.domain.CloudOauth2AuthorizationExample;
import com.dataease.cloud.base.domain.CloudUserLoginLog;
import com.dataease.cloud.base.mapper.CloudOauth2AuthorizationMapper;
import com.dataease.cloud.base.mapper.CloudUserLoginLogMapper;
import com.dataease.cloud.entity.response.Result;
import com.dataease.cloud.i18n.I18nService;
import com.dataease.cloud.utils.AddressUtils;
import com.dataease.cloud.utils.CommonUtils;
import com.dataease.cloud.utils.ObjectMapperUtils;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

/**
 * oidc一个默认的退出filter
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/23 3:00 PM
 **/
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1000)
public class OidcLogoutFilter extends GenericFilterBean {

    private static final String ID_TOKEN_HINT_LOGOUT = "id_token_hint";

    private RequestMatcher logoutRequestMatcher;

    private SessionRegistry sessionRegistry;
    private CloudOauth2AuthorizationMapper cloudOauth2AuthorizationMapper;
    private CloudUserLoginLogMapper cloudUserLoginLogMapper;

    public OidcLogoutFilter(SessionRegistry sessionRegistry,
                            CloudOauth2AuthorizationMapper cloudOauth2AuthorizationMapper,
                            CloudUserLoginLogMapper cloudUserLoginLogMapper) {
        this.cloudOauth2AuthorizationMapper = cloudOauth2AuthorizationMapper;
        this.sessionRegistry = sessionRegistry;
        this.cloudUserLoginLogMapper = cloudUserLoginLogMapper;
        setFilterProcessesUrl("/logout");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    /**
     * 过滤器处理在这里
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (requiresLogout(request, response)) {
            String param = request.getParameter(ID_TOKEN_HINT_LOGOUT);
            if (StringUtils.hasText(param)) {
                //这里面去处理当idtoken传递进来的时候
                CloudOauth2AuthorizationExample example = new CloudOauth2AuthorizationExample();
                example.createCriteria().andOidcIdTokenValueEqualTo(param);
                CloudOauth2Authorization cloudOauth2Authorization = cloudOauth2AuthorizationMapper.selectByExample(example).stream().findFirst().orElse(null);
                if (StringUtils.hasText(cloudOauth2Authorization.getSessionId())) {
                    SessionInformation si = sessionRegistry.getSessionInformation(cloudOauth2Authorization.getSessionId());
                    //触发的前提是这个id-token对应的会话还在线才行
                    if (si != null) {
                        si.expireNow();
                        // 记录登出日志
                        CloudUserLoginLog cloudUserLoginLog = new CloudUserLoginLog();
                        cloudUserLoginLog.setLoginTime(Calendar.getInstance().getTime());
                        cloudUserLoginLog.setUserId(cloudOauth2Authorization.getUserId());
                        cloudUserLoginLog.setSessionId(request.getSession().getId());
                        cloudUserLoginLog.setIp(CommonUtils.getIpAddress(request));
                        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
                        //还需要加其他的信息哦
                        cloudUserLoginLog.setStatus(1);
                        if (userAgent != null) {
                            cloudUserLoginLog.setBrowser(userAgent.getBrowser().getName());//浏览器
                            cloudUserLoginLog.setOs(userAgent.getOperatingSystem().getName());//操作系统
                        }
                        cloudUserLoginLog.setMsg("退出成功");
                        cloudUserLoginLog.setLoginLocation(AddressUtils.getRealAddressByIP(cloudUserLoginLog.getIp()));//登录地址
                        cloudUserLoginLogMapper.insertSelective(cloudUserLoginLog);

                        // 返回信息
                        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                        response.setContentType(MediaType.APPLICATION_JSON.toString());
                        PrintWriter out = response.getWriter();
                        out.write(ObjectMapperUtils.simpleObjectMapper.writeValueAsString(Result.success(I18nService.getMessage("user_logout_success_msg"))));
                        out.flush();
                        out.close();
                        return;
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }

    protected boolean requiresLogout(HttpServletRequest request, HttpServletResponse response) {
        if (this.logoutRequestMatcher.matches(request)) {
            return true;
        }
        if (log.isTraceEnabled()) {
            log.trace(String.format("Did not match oidc logout request to %s", this.logoutRequestMatcher));
        }
        return false;
    }

    public void setFilterProcessesUrl(String filterProcessesUrl) {
        this.logoutRequestMatcher = new AntPathRequestMatcher(filterProcessesUrl);
    }
}
