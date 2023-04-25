package com.dataease.cloud.service.sms.impl;

import cn.hutool.core.util.ReUtil;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.dataease.cloud.constant.RegxContants;
import com.dataease.cloud.constant.SmsSendType;
import com.dataease.cloud.entity.response.AliyunSmsCodeResponse;
import com.dataease.cloud.exception.CloudException;
import com.dataease.cloud.i18n.I18nService;
import com.dataease.cloud.properties.sms.AliyunSmsProperties;
import com.dataease.cloud.properties.SmsProperties;
import com.dataease.cloud.service.sms.MessageService;
import com.dataease.cloud.utils.ObjectMapperUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * 阿里云短信服务
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/11 3:00 PM
 **/
@Slf4j
@Service
@ConditionalOnProperty(prefix = "cloud.sms", name = "provider", havingValue = "aliyun")
public class AliyunMessageServiceImpl implements MessageService {

    @Resource
    private SmsProperties smsProperties;

    @Override
    public void sendCode(SmsSendType sendType, String phone, String code) {
        if (!StringUtils.hasText(phone) || !ReUtil.isMatch(RegxContants.PHONE_NUM_REGX, phone))
            CloudException.throwException(I18nService.getMessage("phone_number_error"));
        //放到这个里面就比较好
        if (ObjectUtils.isEmpty(smsProperties.getAliyun()))
            CloudException.throwException(I18nService.getMessage("aliyun_sms_config_required"));
        AliyunSmsProperties aliyun = smsProperties.getAliyun();
        IClientProfile profile = DefaultProfile.getProfile(aliyun.getRegionId(), aliyun.getAccessKey(), aliyun.getSecretKey());
        DefaultAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", aliyun.getRegionId());
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", aliyun.getSignName());
        String template = aliyun.getDefalutTemplate();
        //这里去根据type获取一下对应的数据即可 有就注入否则直接使用默认即可
        if (aliyun.getTemplate() != null && aliyun.getTemplate().containsKey(sendType)) {
            template = aliyun.getTemplate().get(sendType);
        }
        request.putQueryParameter("TemplateCode", template);
        // 这个地方是JSON格式
        request.putQueryParameter("TemplateParam", String.format("{\"code\":\"%s\"}", code));
        try {
            CommonResponse response = client.getCommonResponse(request);
            AliyunSmsCodeResponse smsCodeResponse = ObjectMapperUtils.simpleObjectMapper.readValue(response.getData(), AliyunSmsCodeResponse.class);
            if ("isv.BUSINESS_LIMIT_CONTROL".equals(smsCodeResponse.getCode()))
                CloudException.throwException(I18nService.getMessage("sending_messages_too_frequently_hour"));
            if ("isv.DAY_LIMIT_CONTROL".equals(smsCodeResponse.getCode()))
                CloudException.throwException(I18nService.getMessage("sending_messages_too_frequently_day"));
            log.info(String.format("response: %s", response.getData()));
        } catch (Exception e) {
            log.error(String.format("阿里云短信发送失败: %s", e.getMessage()));
            CloudException.throwException(I18nService.getMessage("aliyun_send_message_error") + e.getMessage());
        } finally {
            if (client != null)
                client.shutdown();
        }
    }
}
