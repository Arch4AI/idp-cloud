package com.dataease.cloud.service;

import com.dataease.cloud.base.domain.CloudRememberMeTokenExample;
import com.dataease.cloud.base.mapper.CloudRememberMeTokenMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;

/**
 * 更新redis session
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/10 3:21 PM
 **/
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class SessionService {

    @Resource
    private SessionRepository sessionRepository;

    @Resource
    private CloudRememberMeTokenMapper cloudRememberMeTokenMapper;

    /**
     * 只获取用户相关的session集合
     *
     * @param userId
     * @return
     */
    public Set<String> getAllSessions(String userId) {
        return Collections.emptySet();
    }

    /**
     * 根据sessionId查询session
     *
     * @param sessionId
     * @return
     */
    public Session getSessionInformation(String sessionId) {
        //直接查询redis中是否有这个sessinId
        Session session = sessionRepository.findById(sessionId);
        return session;
    }

    /**
     * 踢出session
     * 需要处理一下remberMe里面的数据 因为踢出session remberMe记住我还是会自动登录
     *
     * @param sessionId
     */
    public void kickoutSession(String sessionId) {
        //判断是不是在redis存在
        Session session = sessionRepository.findById(sessionId);
        if (session != null) {
            session.isExpired();
        }
        removeRememberMeTokenBySession(sessionId);
    }

    /**
     * 移除remeberMe的token信息
     *
     * @param sessionId
     */
    public void removeRememberMeTokenBySession(String sessionId) {
        CloudRememberMeTokenExample example = new CloudRememberMeTokenExample();
        example.createCriteria().andSessionIdEqualTo(sessionId);
        cloudRememberMeTokenMapper.deleteByExample(example);
    }

}
