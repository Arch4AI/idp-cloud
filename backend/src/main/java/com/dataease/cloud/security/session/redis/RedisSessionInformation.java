package com.dataease.cloud.security.session.redis;

import com.dataease.cloud.base.mapper.CloudRedisSessionMapper;
import com.dataease.cloud.service.RememberMeTokenService;
import com.dataease.cloud.utils.CommonBeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisSessionRepository;

import java.util.Date;

/**
 * 自定义redis的session信息
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/20 10:20 AM
 **/
@Slf4j
public class RedisSessionInformation extends SessionInformation {
    private RedisSessionRepository sessionRepository;

    public RedisSessionInformation(Session session, RedisSessionRepository sessionRepository) {
        super(resolvePrincipal(session), session.getId(), Date.from(session.getLastAccessedTime()));
        this.sessionRepository = sessionRepository;
    }

    /**
     * 可垃圾了 因为这里可能是一个空的
     *
     * @param session
     * @return
     */
    private static String resolvePrincipal(Session session) {
        SecurityContextImpl springSecurityContext = session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        if (springSecurityContext != null && springSecurityContext.getAuthentication() != null) {
            return springSecurityContext.getAuthentication().getName();
        }
        return "";
    }

    /**
     * 设置他为已到期
     */
    @Override
    public void expireNow() {
        super.expireNow();
        //如果session存在的话 就移除掉
        Session session = sessionRepository.findById(getSessionId());
        if (session != null) {
            sessionRepository.deleteById(session.getId());
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

        try {
            //移除记录
            CloudRedisSessionMapper cloudRedisSessionMapper = CommonBeanFactory.getBean(CloudRedisSessionMapper.class);
            if (cloudRedisSessionMapper != null) {
                cloudRedisSessionMapper.deleteByPrimaryKey(getSessionId());
            }
        } catch (Exception e) {
            log.error("session {} remove redis session user relation error:{}", getSessionId(), e.getMessage());
        }
    }
}
