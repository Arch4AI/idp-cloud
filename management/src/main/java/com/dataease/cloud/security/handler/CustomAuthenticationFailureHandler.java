package com.dataease.cloud.security.handler;

import com.dataease.cloud.base.domain.CloudUserLoginLog;
import com.dataease.cloud.base.mapper.CloudUserLoginLogMapper;
import com.dataease.cloud.constant.Constants;
import com.dataease.cloud.entity.response.Result;
import com.dataease.cloud.i18n.I18nService;
import com.dataease.cloud.security.LocalUser;
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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

/**
 * 认证错误
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/8 9:45 AM
 **/
@Component
@Slf4j
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Resource
    private ObjectMapper jacksonObjectMapper;

    @Resource
    private CloudUserLoginLogMapper cloudUserLoginLogMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON.toString());

        CloudUserLoginLog cloudUserLoginLog = new CloudUserLoginLog();
        cloudUserLoginLog.setLoginTime(Calendar.getInstance().getTime());
        cloudUserLoginLog.setSessionId(request.getSession().getId());
        cloudUserLoginLog.setIp(CommonUtils.getIpAddress(request));

        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        //还需要加其他的信息哦
        cloudUserLoginLog.setStatus(0);
        if (userAgent != null) {
            cloudUserLoginLog.setBrowser(userAgent.getBrowser().getName());//浏览器
            cloudUserLoginLog.setOs(userAgent.getOperatingSystem().getName());//操作系统
        }
        cloudUserLoginLog.setLoginLocation(AddressUtils.getRealAddressByIP(cloudUserLoginLog.getIp()));//登录地址

        Object reqUser = request.getAttribute(Constants.REQ_USER_ATTRIBUTE);
        if (!ObjectUtils.isEmpty(reqUser)) {
            if (reqUser instanceof LocalUser) {
                cloudUserLoginLog.setUserId(((LocalUser) reqUser).getUserId());
            }
        }
        cloudUserLoginLog.setMsg("登录失败：" + exception.getLocalizedMessage());
        cloudUserLoginLogMapper.insertSelective(cloudUserLoginLog);
//        Result result = Result.error(exception, I18nService.getMessage("login_error_msg"));
        Result result = Result.error(exception, getMessageByException(exception));
        PrintWriter out = response.getWriter();
        out.write(jacksonObjectMapper.writeValueAsString(result));
        out.flush();
        out.close();
    }

    /**
     * 根据异常返回提示信息
     *
     * @param exception
     * @return
     */
    private String getMessageByException(AuthenticationException exception){
        String message = I18nService.getMessage("login_error_msg",exception.getMessage());
        return message;
    }
}
