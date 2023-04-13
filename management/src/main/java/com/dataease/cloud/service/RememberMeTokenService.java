package com.dataease.cloud.service;

import com.dataease.cloud.base.domain.CloudRememberMeToken;
import com.dataease.cloud.base.domain.CloudRememberMeTokenExample;
import com.dataease.cloud.base.mapper.CloudRememberMeTokenMapper;
import com.dataease.cloud.base.mapper.CloudSessionMapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private CloudSessionMapper cloudSessionMapper;

    @Resource
    private SessionRepository sessionRepository;


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
        //这个地方需要判断一些东西 如果他的sessionId变更了 说明需要将原来的session移除掉才行 保证一个token绑定到一个session上
        // 这里可以确保用户如果只把remeberMe换到另外一个地方去执行 就只有一个生效 原来的session立即失效 这个和是否多用户登录无关
        CloudRememberMeToken oldToken = cloudRememberMeTokenMapper.selectByPrimaryKey(token.getSeries());
        if (!StringUtils.equals(oldToken.getSessionId(), token.getSessionId())) {
            //移除旧的记录
            Session session = sessionRepository.findById(oldToken.getSessionId());
            if (session != null) {
                sessionRepository.deleteById(oldToken.getSessionId());
            }
            cloudSessionMapper.deleteByPrimaryKey(oldToken.getSessionId());
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
}
