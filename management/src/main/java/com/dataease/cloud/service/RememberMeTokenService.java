package com.dataease.cloud.service;

import com.dataease.cloud.base.domain.CloudRememberMeToken;
import com.dataease.cloud.base.domain.CloudRememberMeTokenExample;
import com.dataease.cloud.base.mapper.CloudRememberMeTokenMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * 记住我的持久化信息
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/12 4:46 PM
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class RememberMeTokenService {

    @Resource
    private CloudRememberMeTokenMapper cloudRememberMeTokenMapper;

    /**
     * 保存记住我的信息
     * @param token
     */
    public void createNewToken(CloudRememberMeToken token) {
        cloudRememberMeTokenMapper.insertSelective(token);
    }

    /**
     * 更新记住我的信息
     * @param token
     */
    public void updateToken(CloudRememberMeToken token) {
        cloudRememberMeTokenMapper.updateByPrimaryKeySelective(token);
    }

    /**
     * 根据系列获取记住我的信息
     * @param seriesId
     * @return
     */
    public CloudRememberMeToken getTokenForSeries(String seriesId) {
        return cloudRememberMeTokenMapper.selectByPrimaryKey(seriesId);
    }

    /**
     * 根据系列移除记住我的信息
     * @param seriesId
     */
    public void removeBySeries(String seriesId) {
        cloudRememberMeTokenMapper.deleteByPrimaryKey(seriesId);
    }

    /**
     * 根据会话ID移除记住我的信息
     * @param sessionId
     */
    public void removeBySessionId(String sessionId){
        CloudRememberMeTokenExample example = new CloudRememberMeTokenExample();
        example.createCriteria().andSessionIdEqualTo(sessionId);
        cloudRememberMeTokenMapper.deleteByExample(example);
    }
}
