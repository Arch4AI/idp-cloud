package com.dataease.cloud.security.session.redisIndex;

import com.dataease.cloud.service.RememberMeTokenService;
import com.dataease.cloud.utils.CommonBeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;

import java.util.Date;

/**
 * 自定义redis_index的session信息
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/20 10:20 AM
 **/
@Slf4j
class RedisIndexSessionInformation<S extends Session> extends SessionInformation {

    static final String EXPIRED_ATTR = RedisIndexSessionInformation.class.getName() + ".EXPIRED";

    private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";

    private final SessionRepository<S> sessionRepository;

    RedisIndexSessionInformation(S session, SessionRepository<S> sessionRepository) {
        super(resolvePrincipal(session), session.getId(), Date.from(session.getLastAccessedTime()));
        this.sessionRepository = sessionRepository;
        Boolean expired = session.getAttribute(EXPIRED_ATTR);
        if (Boolean.TRUE.equals(expired)) {
            super.expireNow();
        }
    }

    /**
     * 解析用户
     *
     * @param session
     * @return
     */
    private static String resolvePrincipal(Session session) {
        String principalName = session.getAttribute(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME);
        if (principalName != null) {
            return principalName;
        }
        SecurityContext securityContext = session.getAttribute(SPRING_SECURITY_CONTEXT);
        if (securityContext != null && securityContext.getAuthentication() != null) {
            return securityContext.getAuthentication().getName();
        }
        return "";
    }

    @Override
    public void expireNow() {
        if (log.isDebugEnabled()) {
            log.debug("Expiring session " + getSessionId() + " for user '" + getPrincipal()
                    + "', presumably because maximum allowed concurrent " + "sessions was exceeded");
        }
        super.expireNow();
        S session = this.sessionRepository.findById(getSessionId());
        if (session != null) {
            session.setAttribute(EXPIRED_ATTR, Boolean.TRUE);
            this.sessionRepository.save(session);
        } else {
            log.info("Could not find Session with id " + getSessionId() + " to mark as expired");
        }
        try {
            //移除remember
            RememberMeTokenService rememberMeTokenService = CommonBeanFactory.getBean(RememberMeTokenService.class);
            if (rememberMeTokenService != null) {
                rememberMeTokenService.removeBySessionId(getSessionId());
            }
        } catch (Exception e) {
            log.error("session {} remove remeberme token error:{}", getSessionId(), e.getMessage());
        }
    }
}
