package com.dataease.cloud.init;

import cn.hutool.core.util.IdUtil;
import com.dataease.cloud.base.domain.*;
import com.dataease.cloud.base.mapper.CloudSocialSettingMapper;
import com.dataease.cloud.base.mapper.CloudUserMapper;
import com.dataease.cloud.base.mapper.CloudUserRoleMapper;
import com.dataease.cloud.constant.GenderEnum;
import com.dataease.cloud.constant.LanguageEnum;
import com.dataease.cloud.constant.RoleEnum;
import com.dataease.cloud.entity.request.RegisteredClientQueryReq;
import com.dataease.cloud.security.social.constant.ClientType;
import com.dataease.cloud.service.Oauth2RegisteredClientSerive;
import com.dataease.cloud.utils.CommonBeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

/**
 * 系统初始化
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/7 2:13 PM
 **/
@Slf4j
public class SystemInit {

    private CloudUserMapper cloudUserMapper;
    private CloudUserRoleMapper cloudUserRoleMapper;
    private static final String DEFAULT_USER = "admin";

    public SystemInit(CloudUserMapper cloudUserMapper, CloudUserRoleMapper cloudUserRoleMapper) {
        this.cloudUserMapper = cloudUserMapper;
        this.cloudUserRoleMapper = cloudUserRoleMapper;
    }

    @EventListener
    public void initialize(ApplicationReadyEvent applicationReadyEvent) {
        // 创建admin
        initAdmin();
        //创建一个RegisteredClient
        initRegisteredClient();
        // 创建一个新的认证
        initSocialClientSetting();
    }

    /**
     * 创建Admin
     */
    private void initAdmin() {
        CloudUserExample cloudUserExample = new CloudUserExample();
        cloudUserExample.createCriteria().andUserNameEqualTo(DEFAULT_USER);
        cloudUserMapper
                .selectByExample(cloudUserExample)
                .stream()
                .findFirst()
                .ifPresentOrElse(cloudUser -> {
                    log.info("用户已经存在:{}", cloudUser);
                }, () -> {
                    // 添加用户
                    CloudUser cloudUser = new CloudUser();
                    cloudUser.setUserId(IdUtil.randomUUID());
                    cloudUser.setUserName(DEFAULT_USER);
                    cloudUser.setEmail(DEFAULT_USER + "@cloud.io");
                    cloudUser.setPhone("13168330000");
                    cloudUser.setEnabled(true);
                    cloudUser.setGender(GenderEnum.MAN.getKey());
                    cloudUser.setLanguage(LanguageEnum.ZH_CN.getName());
                    cloudUser.setNickName("超级管理员");
                    cloudUser.setPassword(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("admin"));
                    cloudUser.setCreateTime(Calendar.getInstance().getTime());
                    cloudUser.setUpdateTime(Calendar.getInstance().getTime());
                    cloudUserMapper.insertSelective(cloudUser);
                    //添加角色
                    CloudUserRole cloudUserRole = new CloudUserRole();
                    cloudUserRole.setUserId(cloudUser.getUserId());
                    cloudUserRole.setRole(RoleEnum.ADMIN.name());
                    cloudUserRole.setCreateTime(Calendar.getInstance().getTime());
                    cloudUserRole.setUpdateTime(Calendar.getInstance().getTime());
                    cloudUserRoleMapper.insertSelective(cloudUserRole);
                });
    }

    /**
     * 初始化一个测试应用客户端
     */
    private void initRegisteredClient() {
        Oauth2RegisteredClientSerive registeredClientSerive = CommonBeanFactory.getBean(Oauth2RegisteredClientSerive.class);
        List<CloudOauth2RegisteredClient> clientList = registeredClientSerive.list(new RegisteredClientQueryReq());
        if (clientList.isEmpty()) {
            CloudOauth2RegisteredClient client = new CloudOauth2RegisteredClient();
            client.setClientId("idp_cloud");
            client.setClientSecret("123456");
            client.setClientName("测试应用");
            client.setRedirectUris(StringUtils.collectionToCommaDelimitedString(new HashSet<>() {{
                add("https://www.baidu.com");
                add("http://127.0.0.1:4000");
            }}));
            registeredClientSerive.createRegisteredClient(client);
        }
    }

    private void initSocialClientSetting() {
        String id = "gitee";
        CloudSocialSetting setting = new CloudSocialSetting();
        setting.setId("gitee");
        setting.setClientId("2a7d3bbe6f10e1bea35a1d0b7e6439bdc073370e9b8dc069d7a8b05bfc525dcc");
        setting.setClientSecret("a40d2efb892a2f96eb9d64989e3ea318a1771645fa52c8cf68ae3643192612a4");
        setting.setClientType(ClientType.oauth2.name());
        setting.setClientName("Gitee");
        setting.setEnable(true);
        setting.setAuthSecret("{}");
        setting.setCreateTime(Calendar.getInstance().getTime());
        setting.setUpdateTime(Calendar.getInstance().getTime());
        setting.setScopes("user_info,emails");
        setting.setAuthGrantType(AuthorizationGrantType.AUTHORIZATION_CODE.getValue());
        setting.setClientAuthMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue());
        setting.setUserAuthMethod(AuthenticationMethod.HEADER.getValue());
        setting.setUserUniqueAttr("login");
        setting.setIcon("icon/gitte.svg");
        setting.setAuthUri("https://gitee.com/oauth/authorize");
        setting.setTokenUri("https://gitee.com/oauth/token");
        setting.setUserInfoUri("https://gitee.com/api/v5/user");
        CloudSocialSettingMapper settingMapper = CommonBeanFactory.getBean(CloudSocialSettingMapper.class);
        if (settingMapper != null) {
            CloudSocialSetting clientSetting = settingMapper.selectByPrimaryKey(id);
            if (clientSetting == null) {
                settingMapper.insertSelective(setting);
            } else {
                settingMapper.updateByPrimaryKeyWithBLOBs(setting);
            }
        }
    }
}
