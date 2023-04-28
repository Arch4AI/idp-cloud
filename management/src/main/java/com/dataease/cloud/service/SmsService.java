package com.dataease.cloud.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.dataease.cloud.constant.Constants;
import com.dataease.cloud.entity.request.SmsRequest;
import com.dataease.cloud.entity.response.SmsCodeResponse;
import com.dataease.cloud.exception.CloudException;
import com.dataease.cloud.i18n.I18nService;
import com.dataease.cloud.service.sms.MessageService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 短信服务
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/9 12:10 PM
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class SmsService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private MessageService messageService;

    /**
     * 发送短信
     * 可能这里到时会有第三方的captcha 需要校验 暂时待定
     */
    public SmsCodeResponse sendCode(SmsRequest smsRequest) {
        if (!StringUtils.hasText(smsRequest.getPhone()))
            CloudException.throwException(I18nService.getMessage("phone_num_is_required"));
        if (stringRedisTemplate.hasKey(smsRequest.getPhone()))
            CloudException.throwException(I18nService.getMessage("code_already_exist"));
        String code = RandomUtil.randomNumbers(6);
        Date offsetMinute = DateUtil.offsetMinute(new Date(), Constants.SMS_CODE_EXPIRE_TIME);
        //发送短信
        messageService.sendCode(smsRequest.getSendType(), smsRequest.getPhone(), code);
        stringRedisTemplate.opsForValue().set(smsRequest.getPhone(), code, Constants.SMS_CODE_EXPIRE_TIME, TimeUnit.MINUTES);
        return SmsCodeResponse.builder()
                .phone(smsRequest.getPhone())
                .expireTime(offsetMinute)
                .build();
    }

    /**
     * 校验短信验证码是否正确
     * 正确后销毁redis中的短信
     *
     * @param smsRequest
     */
    public void checkCode(SmsRequest smsRequest) {
        //获取phone
        if (!StringUtils.hasText(smsRequest.getPhone()))
            CloudException.throwException(I18nService.getMessage("phone_num_is_required"));
        if (!StringUtils.hasText(smsRequest.getCode()))
            CloudException.throwException(I18nService.getMessage("code_is_required"));
        if (!stringRedisTemplate.hasKey(smsRequest.getPhone()))
            CloudException.throwException(I18nService.getMessage("phone_code_is_lost"));
        String code = stringRedisTemplate.opsForValue().get(smsRequest.getPhone());
        if (!code.equals(smsRequest.getCode())) {
            CloudException.throwException(I18nService.getMessage("code_is_not_match"));
        } else {
            //校验成功后销毁
            stringRedisTemplate.delete(smsRequest.getPhone());
        }
    }
}
