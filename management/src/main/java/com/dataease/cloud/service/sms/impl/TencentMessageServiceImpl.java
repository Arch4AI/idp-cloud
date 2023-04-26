package com.dataease.cloud.service.sms.impl;

import com.dataease.cloud.constant.SmsSendType;
import com.dataease.cloud.exception.CloudException;
import com.dataease.cloud.i18n.I18nService;
import com.dataease.cloud.properties.SmsProperties;
import com.dataease.cloud.properties.sms.TencentSmsProperties;
import com.dataease.cloud.service.sms.MessageService;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * 腾讯云短信的实现
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/11 4:19 PM
 **/
@Slf4j
@Service
@ConditionalOnProperty(prefix = "cloud.sms", name = "provider", havingValue = "tencent")
@EnableConfigurationProperties(SmsProperties.class)
public class TencentMessageServiceImpl implements MessageService {

    private SmsProperties smsProperties;

    public TencentMessageServiceImpl(SmsProperties smsProperties){
        this.smsProperties = smsProperties;
        log.info("init tencent sms impl");
    }

    @Override
    public void sendCode(SmsSendType sendType, String phone, String code) {
        if (smsProperties.getTencent() == null)
            CloudException.throwException(I18nService.getMessage("tencent_sms_config_required"));
        String[] templateParamSet = {code};
        String[] phoneNumberSet = {phone};
        TencentSmsProperties tencent = smsProperties.getTencent();
        Credential cred = new Credential(tencent.getAccessKey(), tencent.getSecretKey());
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setReqMethod(HttpMethod.POST.name());
        httpProfile.setConnTimeout(60);
        httpProfile.setEndpoint("sms.tencentcloudapi.com");
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setSignMethod("HmacSHA256");
        clientProfile.setHttpProfile(httpProfile);
        SmsClient client = new SmsClient(cred, "ap-guangzhou", clientProfile);
        SendSmsRequest req = new SendSmsRequest();
        req.setSmsSdkAppId(tencent.getAppId());
        req.setSignName(tencent.getSignName());
        String templateId = tencent.getDefalutTemplate();
        if (tencent.getTemplate() != null && tencent.getTemplate().containsKey(sendType)) {
            templateId = tencent.getTemplate().get(sendType);
        }
        req.setTemplateId(templateId);
        req.setTemplateParamSet(templateParamSet);
        req.setPhoneNumberSet(phoneNumberSet);
        try {
            SendSmsResponse res = client.SendSms(req);
            // 我们发送的时候默认只发送了一个
            for (SendStatus sendStatus : res.getSendStatusSet()) {
                if (!sendStatus.getCode().equalsIgnoreCase(HttpStatus.OK.name())) {
                    CloudException.throwException(I18nService.getMessage("tencent_send_status_error") + sendStatus.getMessage());
                }
            }
        } catch (Exception e) {
            log.error(String.format("腾讯云短信发送失败: %s", e.getMessage()));
            CloudException.throwException(I18nService.getMessage("tencent_send_message_error") + e.getMessage());
        }
    }
}
