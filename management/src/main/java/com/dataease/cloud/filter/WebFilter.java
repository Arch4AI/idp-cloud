package com.dataease.cloud.filter;

import com.dataease.cloud.constant.Constants;
import com.dataease.cloud.entity.response.Result;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.log.LogMessage;
import org.springframework.http.*;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 一个前端的filter
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/31 6:22 PM
 **/
@Slf4j
@Component
public class WebFilter extends OncePerRequestFilter {

    private static final String web_uri  = "http://127.0.0.1:4000";

    private RequestMatcher requiresAuthenticationRequestMatcher = new AndRequestMatcher(new AntPathRequestMatcher(Constants.UI_URI),new MediaTypeRequestMatcher(MediaType.TEXT_HTML));

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 这个是默认的情况
        if (!requiresAuthentication(request, response)) {
            filterChain.doFilter(request, response);
            return;
        }
        String uri = request.getRequestURI();
        RestTemplate restTemplate = new RestTemplate();
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String,Object> header = new HashMap<>();
        if(headerNames.hasMoreElements()){
            String headerName = headerNames.nextElement();
            header.put(headerName,request.getHeader(headerName));
        }
        HttpEntity http = new HttpEntity(header);
        ResponseEntity<String> byteResponseEntity = restTemplate.exchange(web_uri+uri, HttpMethod.GET,http, String.class);
        HttpHeaders httpHeaders = byteResponseEntity.getHeaders();
        for (String h : httpHeaders.keySet()) {
            response.setHeader(h,httpHeaders.getFirst(h));
        }
        PrintWriter out = response.getWriter();
        out.write(byteResponseEntity.getBody());
        out.flush();
        out.close();
    }

    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (this.requiresAuthenticationRequestMatcher.matches(request)) {
            return true;
        }
        if (this.logger.isTraceEnabled()) {
            this.logger
                    .trace(LogMessage.format("Did not match request to %s", this.requiresAuthenticationRequestMatcher));
        }
        return false;
    }
}
