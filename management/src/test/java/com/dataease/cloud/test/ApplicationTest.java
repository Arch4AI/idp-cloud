package com.dataease.cloud.test;

import cn.hutool.core.util.IdUtil;
import com.dataease.cloud.base.domain.CloudExternalAuthSource;
import com.dataease.cloud.service.CloudExternalAuthSourceService;
import com.dataease.cloud.utils.GlobalConfigurations;
import com.fit2cloud.cloud.oauth.config.AuthDefaultSource;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;

/**
 * [描述当前类是什么作用]
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/3 3:09 PM
 **/
@SpringBootTest
public class ApplicationTest {

    @Resource
    private CloudExternalAuthSourceService cloudOauthProviderSettingService;

    @Test
    public void testGetModile() {
        System.out.println(GlobalConfigurations.isReleaseMode());
    }

    @Test
    public void addGithub(){
        CloudExternalAuthSource cloudExternalAuthSource = new CloudExternalAuthSource();
        cloudExternalAuthSource.setId(IdUtil.randomUUID());
        cloudExternalAuthSource.setDisplayName("GitHub");
        cloudExternalAuthSource.setClientId("ec18414ae94c922e24a3");
        cloudExternalAuthSource.setClientSecret("311e811acab444e2c0005219662a89482dd9f939");
        cloudExternalAuthSource.setClientType(AuthDefaultSource.GITHUB.name());
        cloudExternalAuthSource.setRedirectUri("http://127.0.0.1:4000/login");
        cloudExternalAuthSource.setEnable(true);
        cloudExternalAuthSource.setProviderIcon("logo-github-filled");
        cloudOauthProviderSettingService.add(cloudExternalAuthSource);
    }



}
