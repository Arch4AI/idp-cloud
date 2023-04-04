package com.dataease.cloud.security.handler;

import com.dataease.cloud.base.domain.CloudUserLoginLogExample;
import com.dataease.cloud.base.mapper.CloudUserLoginLogMapper;
import com.dataease.cloud.entity.response.Result;
import com.dataease.cloud.service.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 登出成功Handler
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/7 9:28 PM
 **/
@Component
@Slf4j
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Resource
    private CloudUserLoginLogMapper cloudUserLoginLogMapper;

    @Resource
    private ObjectMapper jacksonObjectMapper;

    @Resource
    private SessionService sessionService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String sessionId = request.getSession().getId();
        // 获取客户端的sessionId
        log.info("logout sessionId: {}",sessionId);
        // 获取认证信息里面的details
        log.info("authentication details:{}",jacksonObjectMapper.writeValueAsString(authentication.getDetails()));

        CloudUserLoginLogExample cloudUserLoginLogExample = new CloudUserLoginLogExample();
        cloudUserLoginLogExample.createCriteria().andSessionIdEqualTo(sessionId);

        //如果有就这样吧
        cloudUserLoginLogMapper.selectByExample(cloudUserLoginLogExample).stream().findFirst().ifPresent(cloudUserLoginLog -> {
            cloudUserLoginLog.setLogoutTime(new Date());
            cloudUserLoginLogMapper.updateByPrimaryKeySelective(cloudUserLoginLog);
        });

        //设置sessionId过期
        sessionService.expireNow(request.getRequestedSessionId());

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        PrintWriter out = response.getWriter();
        out.write(jacksonObjectMapper.writeValueAsString(Result.success("用户登出成功")));
        out.flush();
        out.close();
    }
}
