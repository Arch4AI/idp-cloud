package com.dataease.cloud.service;

import com.dataease.cloud.base.domain.CloudRememberMeToken;
import com.dataease.cloud.base.domain.CloudRememberMeTokenExample;
import com.dataease.cloud.base.mapper.CloudRememberMeTokenMapper;
import com.dataease.cloud.constant.LoginType;
import com.dataease.cloud.security.LocalUser;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 记住我的持久化信息
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/12 4:46 PM
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class RememberMeTokenService {

    @Resource
    private CloudRememberMeTokenMapper cloudRememberMeTokenMapper;

    @Resource
    private SessionRepository sessionRepository;

    @Autowired(required = false)
    private SessionRegistry sessionRegistry;


    /**
     * 保存记住我的信息
     *
     * @param token
     */
    public void createNewToken(CloudRememberMeToken token) {
        cloudRememberMeTokenMapper.insertSelective(token);
    }

    /**
     * 更新记住我的信息
     *
     * @param token
     */
    public void updateToken(CloudRememberMeToken token) {
        // 这里可以确保用户如果只把remeberMe换到另外一个地方去执行 就只有一个生效 原来的session立即失效 这个和是否多用户登录无关
        CloudRememberMeToken oldToken = cloudRememberMeTokenMapper.selectByPrimaryKey(token.getSeries());
        if (!StringUtils.equals(oldToken.getSessionId(), token.getSessionId())) {
            //移除旧的记录
            Session session = sessionRepository.findById(oldToken.getSessionId());
            if (session != null) {
                sessionRepository.deleteById(oldToken.getSessionId());
            }
            // 这个地方去保存一下 如果是rememberMe进来的话
            if(sessionRegistry != null){
                LocalUser localUser = new LocalUser();
                localUser.setUserId(token.getUserId());
                localUser.setLoginType(LoginType.REMEMBER_ME);
                sessionRegistry.registerNewSession(token.getSessionId(), localUser);
            }
        }
        cloudRememberMeTokenMapper.updateByPrimaryKeySelective(token);
    }

    /**
     * 根据系列获取记住我的信息
     *
     * @param seriesId
     * @return
     */
    public CloudRememberMeToken getTokenForSeries(String seriesId) {
        return cloudRememberMeTokenMapper.selectByPrimaryKey(seriesId);
    }

    /**
     * 根据系列移除记住我的信息
     *
     * @param seriesId
     */
    public void removeBySeries(String seriesId) {
        cloudRememberMeTokenMapper.deleteByPrimaryKey(seriesId);
    }

    /**
     * 根据会话ID移除记住我的信息
     *
     * @param sessionId
     */
    public void removeBySessionId(String sessionId) {
        CloudRememberMeTokenExample example = new CloudRememberMeTokenExample();
        example.createCriteria().andSessionIdEqualTo(sessionId);
        cloudRememberMeTokenMapper.deleteByExample(example);
    }

    /**
     * 超过最大的时间就移除token信息
     * lastused.gettime < now.gettime - (tokentime*1000)
     * @param maxDate
     */
    public void deleteByMaxDate(Date maxDate){
        CloudRememberMeTokenExample example = new CloudRememberMeTokenExample();
        example.createCriteria().andLastUsedLessThan(maxDate);
        cloudRememberMeTokenMapper.deleteByExample(example);
    }
}
