package com.dataease.cloud.security.session.redisIndex;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 自定义redisIndexSessionRegisteryImpl
 * 处理redisIndex
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/17 2:13 PM
 **/
@Slf4j
public class RedisIndexSessionRegistryImpl<S extends Session> implements SessionRegistry {

    private final FindByIndexNameSessionRepository<S> sessionRepository;

    public RedisIndexSessionRegistryImpl(FindByIndexNameSessionRepository<S> sessionRepository) {
        Assert.notNull(sessionRepository, "sessionRepository cannot be null");
        this.sessionRepository = sessionRepository;
    }

    @Override
    public List<Object> getAllPrincipals() {
        throw new UnsupportedOperationException("RedisIndexSessionRegistryImpl does "
                + "not support retrieving all principals, since Spring Session provides "
                + "no way to obtain that information");
    }

    @Override
    public List<SessionInformation> getAllSessions(Object principal, boolean includeExpiredSessions) {
        Collection<S> sessions = this.sessionRepository.findByPrincipalName(name(principal)).values();
        List<SessionInformation> infos = new ArrayList<>();
        for (S session : sessions) {
            if (includeExpiredSessions
                    || !Boolean.TRUE.equals(session.getAttribute(RedisIndexSessionInformation.EXPIRED_ATTR))) {
                infos.add(new RedisIndexSessionInformation<>(session, this.sessionRepository));
            }
        }
        return infos;
    }

    @Override
    public SessionInformation getSessionInformation(String sessionId) {
        S session = this.sessionRepository.findById(sessionId);
        if (session != null) {
            return new RedisIndexSessionInformation<>(session, this.sessionRepository);
        }
        return null;
    }


    @Override
    public void refreshLastRequest(String sessionId) {
        log.info("session [{}] refresh last request", sessionId);
    }


    @Override
    public void registerNewSession(String sessionId, Object principal) {
        String name = name(principal);
        if (StringUtils.hasText(name)) {
            log.info("user:{} Login,sessionId:{}", name, sessionId);
        }
    }

    @Override
    public void removeSessionInformation(String sessionId) {
        log.info("remove session [{}] info", sessionId);
    }

    protected String name(Object principal) {
        return new TestingAuthenticationToken(principal, null).getName();
    }
}

