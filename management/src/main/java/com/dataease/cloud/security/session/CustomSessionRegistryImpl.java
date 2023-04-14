package com.dataease.cloud.security.session;

import com.dataease.cloud.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.session.Session;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 自定义redisSessionRegisteryImpl
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/17 2:13 PM
 **/
@Slf4j
public class CustomSessionRegistryImpl implements SessionRegistry, ApplicationListener<AbstractSessionEvent> {

    private final SessionService sessionService;

    public CustomSessionRegistryImpl(SessionService sessionService) {
        Assert.notNull(sessionService, "SessionService cannot be null");
        this.sessionService = sessionService;
    }

    @Override
    public List<Object> getAllPrincipals() {
        return Collections.emptyList();
    }

    @Override
    public List<SessionInformation> getAllSessions(Object principal, boolean includeExpiredSessions) {
        if (principal instanceof UserDetails) {
            // 获取sessionId的集合
            Set<String> sessionsUsedByPrincipal = sessionService.getAllSessions(((UserDetails) principal).getUsername());
            if (!sessionsUsedByPrincipal.isEmpty()) {
                List<SessionInformation> list = new ArrayList<>(sessionsUsedByPrincipal.size());
                for (String sessionId : sessionsUsedByPrincipal) {
                    SessionInformation sessionInformation = getSessionInformation(sessionId);
                    if (sessionInformation == null) {
                        continue;
                    }
                    // 是否包含已经移除的session
                    if (includeExpiredSessions || !sessionInformation.isExpired()) {
                        list.add(sessionInformation);
                    }
                }
                return list;
            }
        }
        return Collections.emptyList();
    }

    @Override
    public SessionInformation getSessionInformation(String sessionId) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");
        Session session = sessionService.getSessionInformation(sessionId);
        if (session != null) {
            return new CustomSessionInformation(session, sessionService);
        }
        return null;
    }

    @Override
    public void refreshLastRequest(String sessionId) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");
        SessionInformation info = getSessionInformation(sessionId);
        if (info != null) {
            // 刷新最新的时间
            info.refreshLastRequest();
            // 刷新数据中的数据
            sessionService.refreshLastRequest(sessionId);
        }
    }

    @Override
    public void registerNewSession(String sessionId, Object principal) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");
        Assert.notNull(principal, "Principal required as per interface contract");
        if (getSessionInformation(sessionId) != null) {
            removeSessionInformation(sessionId);
        }
        if (log.isDebugEnabled()) {
            log.debug("Registering session {}, for principal {}", sessionId, principal);
        }
        sessionService.registerNewSession(sessionId);
    }

    @Override
    public void removeSessionInformation(String sessionId) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");
        SessionInformation info = getSessionInformation(sessionId);
        if (info == null) {
            return;
        }
        if (log.isTraceEnabled()) {
            log.debug("Removing session " + sessionId + " from set of registered sessions");
        }
        //这里只是把信息设置为已经超时
        sessionService.removeSessionInformation(sessionId);
    }

    /**
     * //TODO 无法知道这个
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(AbstractSessionEvent event) {
        if (event instanceof SessionDestroyedEvent) {
            log.info("session destoryed");
        } else if (event instanceof SessionCreationEvent) {
            log.info("session created");
        } else if (event instanceof SessionIdChangedEvent) {
            log.info("session id changed");
        }

    }
}

