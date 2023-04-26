package com.dataease.cloud.service.sms.impl;

import com.dataease.cloud.constant.SmsSendType;
import com.dataease.cloud.service.sms.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(prefix = "cloud.sms", name = "provider", havingValue = "local",matchIfMissing = true)
public class DefaultMessageServiceImpl implements MessageService {

    public DefaultMessageServiceImpl(){
        log.info("init defalut sms impl");
    }

    @Override
    public void sendCode(SmsSendType sendType, String phone, String code) {
        log.info("Message send type:{},Phone Num:{},The Code:{}", sendType, phone, code);
    }
}
