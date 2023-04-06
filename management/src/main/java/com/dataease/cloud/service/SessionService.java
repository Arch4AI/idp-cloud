package com.dataease.cloud.service;

import com.dataease.cloud.base.domain.CloudSession;
import com.dataease.cloud.base.domain.CloudSessionExample;
import com.dataease.cloud.base.mapper.CloudSessionMapper;
import com.dataease.cloud.security.LocalUser;
import com.dataease.cloud.security.profile.ProfileAuthenticationToken;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 更新redis session
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/10 3:21 PM
 **/
@Service
@Slf4j
public class SessionService {

    @Resource
    private SessionRepository sessionRepository;

    @Resource
    private CloudSessionMapper cloudSessionMapper;

    /**
     * 更新用户session 默认就开始用的ProflieAuthenticationToken
     *
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
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, springSecurityContext);
        sessionRepository.save(session);
    }

    /**
     * 根据sessionId获取用户的session列表
     *
     * @param userId
     * @return
     */
    public List<CloudSession> findSessionByUserId(String userId) {
        CloudSessionExample example = new CloudSessionExample();
        example.createCriteria().andUserIdEqualTo(userId);
        return cloudSessionMapper.selectByExample(example);
    }

    /**
     * 只获取用户相关的session集合
     *
     * @param userId
     * @return
     */
    public Set<String> getAllSessions(String userId) {
        CloudSessionExample example = new CloudSessionExample();
        // 只查询没有超时的
        example.createCriteria().andUserIdEqualTo(userId).andExpiredEqualTo(false);
        List<CloudSession> cloudSessions = cloudSessionMapper.selectByExample(example);
        return cloudSessions.stream().map(CloudSession::getSessionId).collect(Collectors.toSet());
    }

    public Session getSessionInformation(String sessionId) {
        //直接查询redis中是否有这个sessinId
        Session session = sessionRepository.findById(sessionId);
        if (session != null && session.getAttributeNames().contains(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)) {
            SecurityContextImpl springSecurityContext = session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
            Object principal = springSecurityContext.getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                Duration duration = session.getMaxInactiveInterval();//存活时间间隔
                Instant instant = session.getLastAccessedTime();//最后一次更新的时间
                Instant expireInstant = instant.plusSeconds(duration.toSeconds());
                CloudSession cloudSession = cloudSessionMapper.selectByPrimaryKey(sessionId);
                if (cloudSession == null) {
                    cloudSession = new CloudSession();
                    cloudSession.setSessionId(sessionId);
                    cloudSession.setUserId(((UserDetails) principal).getUsername());
                    cloudSession.setExpireTime(Date.from(expireInstant));
                    cloudSession.setLastQuestTime(Date.from(instant));
                    cloudSession.setExpired(false);
                    cloudSession.setCreateTime(Calendar.getInstance().getTime());
                    cloudSessionMapper.insertSelective(cloudSession);
                } else {
                    cloudSession.setUserId(((UserDetails) principal).getUsername());
                    cloudSession.setExpireTime(Date.from(expireInstant));
                    cloudSession.setLastQuestTime(Date.from(instant));
                    cloudSession.setExpired(false);
                    cloudSessionMapper.updateByPrimaryKeySelective(cloudSession);
                }
                return session;
            }
        } else {
            CloudSession cloudSession = cloudSessionMapper.selectByPrimaryKey(sessionId);
            // 设置已经过期
            if (cloudSession != null) {
                cloudSession.setExpired(true);
                cloudSession.setExpireTime(Calendar.getInstance().getTime());
                cloudSessionMapper.updateByPrimaryKeySelective(cloudSession);
            }
        }
        return null;
    }

    /**
     * 刷新最后一次请求
     * 这个地方暂时未去校验？
     *
     * @param sessionId
     */
    public void refreshLastRequest(String sessionId) {
        Session session = sessionRepository.findById(sessionId);
        if (session != null) {
            CloudSession cloudSession = new CloudSession();
            cloudSession.setSessionId(sessionId);
            cloudSession.setLastQuestTime(Calendar.getInstance().getTime());
            cloudSessionMapper.updateByPrimaryKeySelective(cloudSession);
        } else {
            CloudSession cloudSession = new CloudSession();
            cloudSession.setSessionId(sessionId);
            cloudSession.setExpired(true);
            cloudSession.setExpireTime(Calendar.getInstance().getTime());
            cloudSessionMapper.updateByPrimaryKeySelective(cloudSession);
        }
    }

    /**
     * 直接设置超时
     * //TODO 这个是否直接删除比较合理？
     *
     * @param sessionId
     */
    public void expireNow(String sessionId) {
        sessionRepository.deleteById(sessionId);
        CloudSession cloudSession = new CloudSession();
        cloudSession.setSessionId(sessionId);
        cloudSession.setExpired(true);
        cloudSession.setExpireTime(Calendar.getInstance().getTime());
        cloudSessionMapper.updateByPrimaryKeySelective(cloudSession);
    }

    /**
     * 保存session信息
     *
     * @param sessionId
     */
    public void registerNewSession(String sessionId) {
        Session session = sessionRepository.findById(sessionId);
        if (session != null) {
            SecurityContextImpl springSecurityContext = session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
            Object principal = springSecurityContext.getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                Duration duration = session.getMaxInactiveInterval();//存活时间间隔
                Instant instant = session.getLastAccessedTime();//最后一次更新的时间
                Instant expireInstant = instant.plusSeconds(duration.toSeconds());
                CloudSession cloudSession = cloudSessionMapper.selectByPrimaryKey(sessionId);
                if (cloudSession == null) {
                    cloudSession = new CloudSession();
                    cloudSession.setSessionId(sessionId);
                    cloudSession.setUserId(((UserDetails) principal).getUsername());
                    cloudSession.setExpireTime(Date.from(expireInstant));
                    cloudSession.setLastQuestTime(Date.from(instant));
                    cloudSession.setExpired(false);
                    cloudSession.setCreateTime(Calendar.getInstance().getTime());
                    cloudSessionMapper.insertSelective(cloudSession);
                } else {
                    cloudSession.setUserId(((UserDetails) principal).getUsername());
                    cloudSession.setExpireTime(Date.from(expireInstant));
                    cloudSession.setLastQuestTime(Date.from(instant));
                    cloudSession.setExpired(false);
                    cloudSessionMapper.updateByPrimaryKeySelective(cloudSession);
                }
            }
        }
    }

    /**
     * 移除sessionId信息 或者直接改成已经没了？
     *
     * @param sessionId
     */
    public void removeSessionInformation(String sessionId) {
        CloudSession cloudSession = new CloudSession();
        cloudSession.setExpireTime(Calendar.getInstance().getTime());
        cloudSession.setSessionId(sessionId);
        cloudSession.setExpired(true);
        cloudSessionMapper.updateByPrimaryKeySelective(cloudSession);
        //在session里面也移除掉他
        sessionRepository.deleteById(sessionId);
    }

    /**
     * 添加或者更新session
     *
     * @param session
     * @param userId
     */
    public void addOrUpdateSessionRecord(HttpSession session, String userId) {
        CloudSession cloudSession = cloudSessionMapper.selectByPrimaryKey(session.getId());
        if (cloudSession != null) {
            cloudSession.setLastQuestTime(Calendar.getInstance().getTime());
            cloudSession.setUserId(userId);
            cloudSessionMapper.updateByPrimaryKeySelective(cloudSession);
        } else {
            cloudSession = new CloudSession();
            int duration = session.getMaxInactiveInterval();//存活时间间隔
            Long instant = session.getLastAccessedTime();//最后一次更新的时间
            Instant expireInstant = Instant.ofEpochMilli(instant).plusSeconds(duration);
            cloudSession.setUserId(userId);
            cloudSession.setSessionId(session.getId());
            cloudSession.setExpired(false);
            cloudSession.setLastQuestTime(Calendar.getInstance().getTime());
            cloudSession.setCreateTime(Calendar.getInstance().getTime());
            cloudSession.setExpireTime(Date.from(expireInstant));
            cloudSessionMapper.insertSelective(cloudSession);
        }
    }

    /**
     * 判断Session是否过期
     * 在redis里面
     *
     * @param sessionId
     * @return
     */
    public boolean sessionHasdestroyed(String sessionId) {
        Session session = sessionRepository.findById(sessionId);
        return session == null || session.isExpired();
    }

    /**
     * 销毁session
     *
     * @param sessionId
     */
    public void destroyeSession(String sessionId) {
        sessionRepository.deleteById(sessionId);
    }

}
