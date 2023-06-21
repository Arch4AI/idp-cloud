package com.dataease.cloud.test;

import com.dataease.cloud.i18n.I18nService;
import com.dataease.cloud.properties.SmsProperties;
import com.dataease.cloud.service.SocialAuthenticationService;
import com.dataease.cloud.service.sms.MessageService;
import com.dataease.cloud.utils.GlobalConfigurations;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * 单元测试类
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/3 3:09 PM
 **/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {

    @Resource
    private SocialAuthenticationService cloudOauthProviderSettingService;

    @Resource
    private SmsProperties smsProperties;

    //    @Autowired(required = false)
    @Resource
    private MessageService messageService;

    @Test
    public void testGetModile() {
        System.out.println(GlobalConfigurations.isReleaseMode());
    }

    @Test
    public void addGithub() {
    }


    @Test
    public void getSmsConfig() {
        if (messageService == null)
            System.out.println("message not init");
        String simpleName = messageService.getClass().getSimpleName();
        System.out.println("message provider:" + simpleName);
    }

    @Test
    public void getI18n() {
        LocaleContextHolder.setLocale(Locale.US);
        System.out.println(I18nService.getMessage("user_not_login"));
    }


}
