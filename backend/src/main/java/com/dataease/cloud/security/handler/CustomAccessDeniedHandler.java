package com.dataease.cloud.security.handler;

import com.dataease.cloud.entity.response.Result;
import com.dataease.cloud.i18n.I18nService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 权限不够处理
 * 默认返回401
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/7 9:04 PM
 **/
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Resource
    private ObjectMapper jacksonObjectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        PrintWriter out = response.getWriter();
        out.write(jacksonObjectMapper.writeValueAsString(Result.error(accessDeniedException, I18nService.getMessage("access_denied_msg"))));
        out.flush();
        out.close();
    }
}
