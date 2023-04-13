package com.dataease.cloud.service.sms.impl;

import com.dataease.cloud.constant.SmsSendType;
import com.dataease.cloud.service.sms.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

/**
 * 本地的短信服务
 * 这个只在日志中打印一下该要的即可
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/12 9:58 AM
 **/
@Slf4j
@Service
@ConditionalOnMissingBean({AliyunMessageServiceImpl.class, TencentMessageServiceImpl.class})
public class DefaultMessageServiceImpl implements MessageService {

    @Override
    public void sendCode(SmsSendType sendType, String phone, String code) {
        log.info("Message send type:{},Phone Num:{},The Code:{}", sendType, phone, code);
    }
}
