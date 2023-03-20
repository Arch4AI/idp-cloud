package com.dataease.cloud.security.session;

import com.dataease.cloud.service.SessionService;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.Session;

import java.util.Date;

/**
 * 自定义的session信息
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/20 10:20 AM
 **/
public class CustomSessionInformation extends SessionInformation {

    private SessionService sessionService;

    public CustomSessionInformation(Session session, SessionService sessionService) {
        super(resolvePrincipal(session), session.getId(), Date.from(session.getLastAccessedTime()));
        this.sessionService = sessionService;
    }

    /**
     * 解析一下把
     * @param session
     * @return
     */
    private static Object resolvePrincipal(Session session) {
        SecurityContextImpl springSecurityContext = session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        Object principal = springSecurityContext.getAuthentication().getPrincipal();
        return principal;
    }

    /**
     * 设置他为已到期
     */
    @Override
    public void expireNow() {
        sessionService.expireNow(this.getSessionId());
        super.expireNow();
    }
}
