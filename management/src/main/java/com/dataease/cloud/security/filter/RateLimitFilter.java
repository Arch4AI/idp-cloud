package com.dataease.cloud.security.filter;

import com.dataease.cloud.entity.response.Result;
import com.dataease.cloud.i18n.I18nService;
import com.dataease.cloud.properties.CommonProperties;
import com.dataease.cloud.utils.IpUtils;
import com.dataease.cloud.utils.ObjectMapperUtils;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Filter限流服务
 * 排除从controller那边定义的URL
 * 目前只支持已IP为主键进行限流
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/17 4:56 PM
 **/
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(prefix = "cloud.common", name = "enable-rate-limit", havingValue = "true")
public class RateLimitFilter implements Filter, ApplicationContextAware {

    //采用线程安全的Map会占用大量内存
    private static ConcurrentMap<String, RateLimiter> RATE_LIMITER_CACHE = new ConcurrentHashMap<>();
    private ApplicationContext applicationContext;
    private List<RequestMappingInfo> requestMappingInfoList = new ArrayList<>();


    /**
     * 是不是在requestMapping中
     *
     * @param request
     * @return
     */
    private boolean requestMappingExist(HttpServletRequest request) {
        //先获取一次
        getRequestMapping();
        for (RequestMappingInfo requestMappingInfo : requestMappingInfoList) {
            String path = requestMappingInfo.getPathPatternsCondition().getFirstPattern().getPatternString();
            String method = requestMappingInfo.getMethodsCondition() != null ? requestMappingInfo.getMethodsCondition().getMethods().stream().findFirst().get().name() : HttpMethod.GET.name();
            //获取到path/method
            AntPathRequestMatcher antPathRequestMatcher = new AntPathRequestMatcher(path, method);
            if (antPathRequestMatcher.matcher(request).isMatch()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取一下requestMapping的信息
     */
    private void getRequestMapping() {
        if (!requestMappingInfoList.isEmpty()) {
            return;
        }
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> methodMap = mapping.getHandlerMethods();
        for (RequestMappingInfo requestMappingInfo : methodMap.keySet()) {
            if (!requestMappingInfo.getMethodsCondition().isEmpty()) {
                requestMappingInfoList.add(requestMappingInfo);
            }
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        log.info("request uri:{},request method:{}", request.getRequestURI(), request.getMethod());
        if (!requestMappingExist(request)) {
            log.info("request not in controller");
            //获取key
            String key = IpUtils.getIpAddr(request);
            if (RATE_LIMITER_CACHE.get(key) == null) {
                RATE_LIMITER_CACHE.put(key, RateLimiter.create(CommonProperties.getRateLimitQps()));
            } else {
                if (!RATE_LIMITER_CACHE.get(key).tryAcquire(CommonProperties.getRateLimitTime(), CommonProperties.getRateLimitTimeUnit())) {
                    response.setStatus(HttpStatus.OK.value());
                    response.setContentType(MediaType.APPLICATION_JSON.toString());
                    PrintWriter out = response.getWriter();
                    out.write(ObjectMapperUtils.simpleObjectMapper.writeValueAsString(Result.error(I18nService.getMessage("rate_many_request_msg"))));
                    out.flush();
                    out.close();
                    return;
                }
            }
        } else {
            log.info("request in to controller");
        }
        filterChain.doFilter(request, response);
    }
}
