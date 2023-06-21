package com.dataease.cloud.service;

import com.dataease.cloud.base.domain.*;
import com.dataease.cloud.base.mapper.CloudSocialSettingMapper;
import com.dataease.cloud.base.mapper.CloudUserSocialMapper;
import com.dataease.cloud.i18n.I18nService;
import com.dataease.cloud.security.LocalUser;
import com.dataease.cloud.security.exception.SecurityException;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.List;

/**
 * 第三方认证源服务
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/13 3:18 PM
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class SocialAuthenticationService {

    @Resource
    private CloudUserSocialMapper cloudUserSocialMapper;

    @Resource
    private CloudSocialSettingMapper cloudSocialSettingMapper;

    @Resource
    private UserService userService;


    /**
     * 绑定信息，必须是没有绑定过的
     *
     * @param userId         用户ID
     * @param uuid           社交用户uuid
     * @param registrationId 社交登录配置ID
     * @param raw            社交用户原始数据
     */
    public void bindNewUser(String userId, String uuid, String registrationId, String raw) {
        CloudUser cloudUser = userService.findById(userId);
        if (cloudUser == null)
            SecurityException.throwException(I18nService.getMessage("user_not_found"));
        if (!cloudUser.getEnabled())
            SecurityException.throwException(I18nService.getMessage("user_has_disabled"));
        CloudUserSocialKey key = new CloudUserSocialKey();
        key.setSocialSettingId(registrationId);
        key.setSocialUserUuid(uuid);
        CloudUserSocial cloudUserSocial = cloudUserSocialMapper.selectByPrimaryKey(key);
        if (cloudUserSocial != null)
            SecurityException.throwException(I18nService.getMessage("auth_user_has_binded"));
        cloudUserSocial = new CloudUserSocial();
        cloudUserSocial.setSocialUserUuid(uuid);
        cloudUserSocial.setSocialSettingId(registrationId);
        cloudUserSocial.setUserId(userId);
        cloudUserSocial.setUserRaw(raw);
        cloudUserSocial.setCreateTime(Calendar.getInstance().getTime());
        cloudUserSocial.setUpdateTime(Calendar.getInstance().getTime());
        cloudUserSocialMapper.insertSelective(cloudUserSocial);
    }

    /**
     * 判断是否是当前用户
     *
     * @param userId
     * @param uuid
     * @param registrationId
     * @return
     */
    public boolean isCurrentUser(String userId, String uuid, String registrationId) {
        CloudUserSocialKey key = new CloudUserSocialKey();
        key.setSocialSettingId(registrationId);
        key.setSocialUserUuid(uuid);
        CloudUserSocial cloudUserSocial = cloudUserSocialMapper.selectByPrimaryKey(key);
        return cloudUserSocial != null && userId.equals(cloudUserSocial.getUserId());
    }

    /**
     * 根据这个去获取信息
     *
     * @param uuid           社交用户uuid
     * @param registrationId 社交登录配置
     * @return
     */
    public LocalUser queryUserBySocailUser(String uuid, String registrationId) {
        CloudUserSocialKey key = new CloudUserSocialKey();
        key.setSocialSettingId(registrationId);
        key.setSocialUserUuid(uuid);
        CloudUserSocial cloudUserSocial = cloudUserSocialMapper.selectByPrimaryKey(key);
        if (cloudUserSocial == null || !StringUtils.hasText(cloudUserSocial.getUserId())) {
            SecurityException.throwException(I18nService.getMessage("user_not_found"));
        }
        //返回LocalUser
        return userService.getUserById(cloudUserSocial.getUserId());
    }

    /**
     * 获取可用的配置
     *
     * @return
     */
    public List<CloudSocialSetting> listActive() {
        CloudSocialSettingExample socialSettingExample = new CloudSocialSettingExample();
        socialSettingExample.createCriteria().andEnableEqualTo(true);
        return cloudSocialSettingMapper.selectByExample(socialSettingExample);
    }

    /**
     * 保存社交信息
     *
     * @param uuid
     * @param registrationId
     * @param raw
     */
    public void saveSocialUser(String uuid, String registrationId, String raw) {
        CloudUserSocial cloudUserSocial = new CloudUserSocial();
        cloudUserSocial.setSocialUserUuid(uuid);
        cloudUserSocial.setSocialSettingId(registrationId);
        cloudUserSocial.setUserRaw(raw);
        cloudUserSocial.setCreateTime(Calendar.getInstance().getTime());
        cloudUserSocial.setUpdateTime(Calendar.getInstance().getTime());
        cloudUserSocialMapper.insertSelective(cloudUserSocial);
    }

    /**
     * 如果这个第三方已经绑定直接报错
     *
     * @param userId         用户ID
     * @param registrationId
     * @param uuid
     */
    public void bindSocailLocalUser(String userId, String registrationId, String uuid) {
        //最好判断一下是不是已经绑定了
        CloudUserSocialKey cloudUserSocialKey = new CloudUserSocialKey();
        cloudUserSocialKey.setSocialSettingId(registrationId);
        cloudUserSocialKey.setSocialUserUuid(uuid);
        CloudUserSocial cloudUserSocial = cloudUserSocialMapper.selectByPrimaryKey(cloudUserSocialKey);
        if (StringUtils.hasText(cloudUserSocial.getUserId()))
            SecurityException.throwException(I18nService.getMessage("external_user_has_bind"));
        cloudUserSocial.setUserId(userId);
        cloudUserSocial.setUpdateTime(Calendar.getInstance().getTime());
        cloudUserSocialMapper.updateByPrimaryKeySelective(cloudUserSocial);
    }

}
