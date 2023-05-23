package com.dataease.cloud.controller;

import com.dataease.cloud.annotation.SysLog;
import com.dataease.cloud.constant.LogOperationType;
import com.dataease.cloud.entity.request.SmsRequest;
import com.dataease.cloud.entity.response.Result;
import com.dataease.cloud.entity.response.SmsCodeResponse;
import com.dataease.cloud.service.SmsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短信服务
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/7 3:42 PM
 **/
@RestController
@RequestMapping("sms")
public class SmsController {

    @Resource
    private SmsService smsService;

    /**
     * 发送短信
     *
     * @param smsRequest
     * @return
     */
    @SysLog(title = "发送短信", type = LogOperationType.SEND_SMS)
    @PostMapping("/send")
    public Result<SmsCodeResponse> send(@RequestBody SmsRequest smsRequest) {
        return Result.success(smsService.sendCode(smsRequest));
    }

    /**
     * 短信校验
     *
     * @param smsRequest
     * @return
     */
    @SysLog(title = "短信校验", type = LogOperationType.VERIFY)
    @PostMapping("/check")
    public Result check(@RequestBody SmsRequest smsRequest) {
        smsService.checkCode(smsRequest);
        return Result.success("校验成功");
    }
}
