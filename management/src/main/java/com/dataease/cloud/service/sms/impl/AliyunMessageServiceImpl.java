package com.dataease.cloud.service.sms.impl;

import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.JSON;
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
import com.dataease.cloud.properties.AliyunSmsProperties;
import com.dataease.cloud.properties.SmsProperties;
import com.dataease.cloud.service.sms.MessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
@Service
@Slf4j
public class AliyunMessageServiceImpl implements MessageService {

    @Resource
    private SmsProperties smsProperties;

    @Override
    public void sendCode(SmsSendType sendType, String phone, String code) {
        if (!StringUtils.hasText(phone) || !ReUtil.isMatch(RegxContants.PHONE_NUM_REGX, phone))
            CloudException.throwException("phone number error");
        //放到这个里面就比较好
        if (ObjectUtils.isEmpty(smsProperties.getAliyun()))
            CloudException.throwException("aliyun sms config required");
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
            AliyunSmsCodeResponse smsCodeResponse = JSON.parseObject(response.getData(), AliyunSmsCodeResponse.class);
            if ("isv.BUSINESS_LIMIT_CONTROL".equals(smsCodeResponse.getCode()))
                CloudException.throwException("发送短信次数已超过每小时限制，请稍后再试或使用其他方式登录！");
            if ("isv.DAY_LIMIT_CONTROL".equals(smsCodeResponse.getCode()))
                CloudException.throwException("发送短信次数已超过每日限制，请使用其他方式登录！");
            log.info(String.format("response: %s", response.getData()));
        } catch (Exception e) {
            log.error(String.format("阿里云短信发送失败: %s", e.getMessage()));
            CloudException.throwException("Aliyun Send Message error:" + e.getMessage());
        } finally {
            if (client != null)
                client.shutdown();
        }
    }
}
