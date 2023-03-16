package com.dataease.cloud.service;

import com.dataease.cloud.security.LocalUser;
import com.dataease.cloud.security.profile.ProfileAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Service;

/**
 * 更新redis session
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/10 3:21 PM
 **/
@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    /**
     * 更新用户session 默认就开始用的ProflieAuthenticationToken
     * @param sessionId 指定SessionId的权限 这样改了以后应该是立马就生效啦
     * @param localUser
     */
    public void updateSession(String sessionId, LocalUser localUser) {
        Session session = sessionRepository.findById(sessionId);
        localUser.setPassword(null);
        SecurityContextImpl springSecurityContext = session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        ProfileAuthenticationToken proflieAuthenticationToken = new ProfileAuthenticationToken(localUser, localUser.getAuthorities());
        proflieAuthenticationToken.setAuthenticated(true);
        proflieAuthenticationToken.setDetails(springSecurityContext.getAuthentication().getDetails());
        springSecurityContext.setAuthentication(proflieAuthenticationToken);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,springSecurityContext);
        sessionRepository.save(session);
    }

    /**
     * 判断Session是否过期
     * 在redis里面
     * @param sessionId
     * @return
     */
    public boolean sessionHasdestroyed(String sessionId){
        Session session = sessionRepository.findById(sessionId);
        return session == null || session.isExpired();
    }

    /**
     * 销毁session
     * @param sessionId
     */
    public void destroyeSession(String sessionId){
        sessionRepository.deleteById(sessionId);
    }

}
