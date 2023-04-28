package com.dataease.cloud.init;

import cn.hutool.core.util.IdUtil;
import com.dataease.cloud.base.domain.*;
import com.dataease.cloud.base.mapper.CloudUserMapper;
import com.dataease.cloud.base.mapper.CloudUserRoleMapper;
import com.dataease.cloud.constant.GenderEnum;
import com.dataease.cloud.constant.LanguageEnum;
import com.dataease.cloud.constant.RoleEnum;
import com.dataease.cloud.entity.request.RegisteredClientQueryReq;
import com.dataease.cloud.service.ExternalAuthSourceService;
import com.dataease.cloud.service.Oauth2RegisteredClientSerive;
import com.dataease.cloud.utils.CommonBeanFactory;
import com.fit2cloud.cloud.oauth.config.AuthDefaultSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
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
    private ExternalAuthSourceService externalAuthSourceService;
    private static final String DEFAULT_USER = "admin";

    public SystemInit(CloudUserMapper cloudUserMapper, CloudUserRoleMapper cloudUserRoleMapper, ExternalAuthSourceService externalAuthSourceService) {
        this.cloudUserMapper = cloudUserMapper;
        this.cloudUserRoleMapper = cloudUserRoleMapper;
        this.externalAuthSourceService = externalAuthSourceService;
    }

    @EventListener
    public void initialize(ApplicationReadyEvent applicationReadyEvent) {
        // 创建admin
        initAdmin();
        // 创建一个keycloak的认证
        initDefaultProvider();
        //创建一个RegisteredClient
        initRegisteredClient();
    }

    /**
     * 初始化一个提供商
     */
    private void initDefaultProvider() {
        // 为空的时候才去注入
        if (externalAuthSourceService.listAll().isEmpty()) {
            CloudExternalAuthSource setting = new CloudExternalAuthSource();
            setting.setId(IdUtil.randomUUID());
            setting.setDisplayName("MeterSphere Cloud Keycloak");
            setting.setClientId("halo");
            setting.setClientSecret("dd942762-b30e-4e34-b2c4-d71daab393b8");
            setting.setClientType(AuthDefaultSource.OIDC.name());
            setting.setAuthUri("https://msdev.xgg.plus/auth/realms/halo/protocol/openid-connect/auth");
            setting.setTokenUri("https://msdev.xgg.plus/auth/realms/halo/protocol/openid-connect/token");
            setting.setUserInfoUri("https://msdev.xgg.plus/auth/realms/halo/protocol/openid-connect/userinfo");
            setting.setRedirectUri("https://www.baidu.com");
            setting.setEnable(true);
            externalAuthSourceService.add(setting);
        }
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
}
