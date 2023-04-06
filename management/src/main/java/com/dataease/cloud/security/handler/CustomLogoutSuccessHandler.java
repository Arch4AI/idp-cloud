package com.dataease.cloud.security.handler;

import com.dataease.cloud.base.domain.CloudUserLoginLog;
import com.dataease.cloud.base.mapper.CloudUserLoginLogMapper;
import com.dataease.cloud.entity.response.Result;
import com.dataease.cloud.security.LocalUser;
import com.dataease.cloud.service.SessionService;
import com.dataease.cloud.utils.AddressUtils;
import com.dataease.cloud.utils.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

/**
 * 登出成功Handler
 *
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
        log.info("logout sessionId: {}", sessionId);
        // 获取认证信息里面的details
        log.info("authentication details:{}", jacksonObjectMapper.writeValueAsString(authentication.getDetails()));

        if(authentication.getPrincipal() instanceof UserDetails){
            UserDetails userDetails = (UserDetails)authentication.getPrincipal();
            CloudUserLoginLog cloudUserLoginLog = new CloudUserLoginLog();
            cloudUserLoginLog.setLoginTime(Calendar.getInstance().getTime());
            cloudUserLoginLog.setUserId(userDetails.getUsername());
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
        }


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
