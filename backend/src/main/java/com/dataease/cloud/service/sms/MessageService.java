package com.dataease.cloud.service.sms;

import com.dataease.cloud.constant.SmsSendType;

/**
 * 短信服务
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/11 2:59 PM
 **/
public interface MessageService {


    /**
     * 发送短信验证码
     *
     * @param sendType 发送的类型 Like {@link SmsSendType#Login}
     * @param phone    手机号码
     * @param code     验证码
     */
    public void sendCode(SmsSendType sendType, String phone, String code);
}
