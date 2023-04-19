package com.dataease.cloud.security.session.redis;

import com.dataease.cloud.base.domain.CloudRedisSession;
import com.dataease.cloud.base.domain.CloudRedisSessionExample;
import com.dataease.cloud.base.mapper.CloudRedisSessionMapper;
import com.dataease.cloud.properties.SessionProperties;
import com.dataease.cloud.utils.CommonBeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisSessionRepository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 自定义redisSessionRegisteryImpl
 * 关联部分使用了jdbc中的cloud_redis_session可以提供性能
 * //TODO 需要定时处理cloud_redis_session中的关联是否已经到期 后续通过定时任务去处理
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/17 2:13 PM
 **/
@Slf4j
public class RedisSessionRegistryImpl implements SessionRegistry {

    private RedisSessionRepository sessionRepository;
    static final String ATTRIBUTE_PREFIX = "sessionAttr:";
    static final String SESSION_ID_KEY = "sessionId";

    public RedisSessionRegistryImpl(RedisSessionRepository sessionRepository) {
        Assert.notNull(sessionRepository, "sessionRepository cannot be null");
        this.sessionRepository = sessionRepository;
    }

    /**
     * 获取所有的session 获取所有的用户 应该说
     * 暂时没有地方会用到
     *
     * @return
     */
    @Override
    public List<Object> getAllPrincipals() {
        String baseKey = SessionProperties.getRedisNamespace() + ":sessions:";
        String redisNameKey = baseKey + "*";
        Set<String> keys = sessionRepository.getSessionRedisOperations().keys(redisNameKey);
        List<Object> sessionList = new ArrayList<>();
        for (String key : keys) {
            String sessionId = key.substring(baseKey.length());
            Session redisSession = sessionRepository.findById(sessionId);
            sessionList.add(redisSession);
        }
        return sessionList;
    }

    /**
     * 获取某一个用户下面所有的session
     *
     * @param principal
     * @param includeExpiredSessions
     * @return
     */
    @Override
    public List<SessionInformation> getAllSessions(Object principal, boolean includeExpiredSessions) {
        List<SessionInformation> sessionInformations = new ArrayList<>();
        String userId = userId(principal);
        if (StringUtils.hasText(userId)) {
            //根据这个去查询本地所有的记录
            try {
                CloudRedisSessionMapper cloudRedisSessionMapper = CommonBeanFactory.getBean(CloudRedisSessionMapper.class);
                if (cloudRedisSessionMapper != null) {
                    CloudRedisSessionExample example = new CloudRedisSessionExample();
                    example.createCriteria().andUserIdEqualTo(userId);
                    List<CloudRedisSession> redisSessions = cloudRedisSessionMapper.selectByExample(example);
                    for (CloudRedisSession redisSession : redisSessions) {
                        SessionInformation sessionInformation = getSessionInformation(redisSession.getSessionId());
                        if (sessionInformation == null) {
                            cloudRedisSessionMapper.deleteByPrimaryKey(redisSession.getSessionId());
                            continue;
                        }
                        sessionInformations.add(sessionInformation);
                    }
                }
            } catch (Exception e) {
                log.error("get user [{}] session info error:{}", userId, e.getMessage());
            }
        }
        return sessionInformations;
    }

    @Override
    public SessionInformation getSessionInformation(String sessionId) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");
        Session session = this.sessionRepository.findById(sessionId);
        if (session != null) {
            return new RedisSessionInformation(session, this.sessionRepository);
        }
        return null;
    }

    @Override
    public void refreshLastRequest(String sessionId) {
        //暂时不确定是否需要去刷新remeber的可用时间
        log.info("session [{}] refresh last request", sessionId);
    }

    @Override
    public void registerNewSession(String sessionId, Object principal) {
        String userId = userId(principal);
        if (StringUtils.hasText(userId)) {
            log.info("user:{} Login,sessionId:{}", userId, sessionId);
            try {
                CloudRedisSessionMapper cloudRedisSessionMapper = CommonBeanFactory.getBean(CloudRedisSessionMapper.class);
                if (cloudRedisSessionMapper != null) {
                    CloudRedisSession cloudRedisSession = new CloudRedisSession();
                    cloudRedisSession.setSessionId(sessionId);
                    cloudRedisSession.setUserId(userId);
                    cloudRedisSessionMapper.insertSelective(cloudRedisSession);
                }
            } catch (Exception e) {
                log.error("save redis session user relation error:", e);
            }
        }
    }

    @Override
    public void removeSessionInformation(String sessionId) {
        log.info("remove session [{}] info", sessionId);
    }

    /**
     * 获取userId
     * @param principal
     * @return
     */
    protected String userId(Object principal) {
        return new TestingAuthenticationToken(principal, null).getName();
    }
}

