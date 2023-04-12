package com.dataease.cloud.service;

import com.dataease.cloud.base.domain.CloudSession;
import com.dataease.cloud.base.domain.CloudSessionExample;
import com.dataease.cloud.base.mapper.CloudSessionMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional(rollbackFor = Exception.class)
public class SessionService {

    @Resource
    private SessionRepository sessionRepository;

    @Resource
    private CloudSessionMapper cloudSessionMapper;

    @Resource
    private RememberMeTokenService rememberMeTokenService;

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

    /**
     * 根据sessionId查询session
     * @param sessionId
     * @return
     */
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
        //如果是退出这个地方也移除掉rememberMe的信息 让那个人直接登录不了即可
        rememberMeTokenService.removeBySessionId(sessionId);
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
     * 获取session列表
     * @return
     */
    public List<CloudSession> listSessions(){
        CloudSessionExample example = new CloudSessionExample();
        return cloudSessionMapper.selectByExample(example);
    }


    /**
     * 获取当前用户所有的会话记录
     * 包含已经到期和正在使用的
     * @param userId
     * @return
     */
    public List<CloudSession> listSessionByUserId(String userId){
        CloudSessionExample example = new CloudSessionExample();
        example.createCriteria().andUserIdEqualTo(userId);
        return cloudSessionMapper.selectByExample(example);
    }

    /**
     * 踢出session
     * 需要处理一下remberMe里面的数据 因为踢出session remberMe记住我还是会自动登录
     * @param sessionId
     */
    public void kickoutSession(String sessionId){
       //判断是不是在redis存在
        log.info("sessionId:{}",sessionId);
        Session session = sessionRepository.findById(sessionId);
        if(session != null){
            session.isExpired();
        }
        CloudSession cloudSession = new CloudSession();
        cloudSession.setExpireTime(Calendar.getInstance().getTime());
        cloudSession.setExpired(true);
        cloudSession.setSessionId(sessionId);
        cloudSessionMapper.updateByPrimaryKeySelective(cloudSession);
        rememberMeTokenService.removeBySessionId(sessionId);
    }

}
