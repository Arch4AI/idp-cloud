package com.dataease.cloud.controller;

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
     * @param smsRequest
     * @return
     */
    @PostMapping("/send")
    public Result<SmsCodeResponse> send(@RequestBody SmsRequest smsRequest){
        return Result.success(smsService.sendCode(smsRequest));
    }

    /**
     * 短信校验
     * @param smsRequest
     * @return
     */
    @PostMapping("/check")
    public Result check(@RequestBody SmsRequest smsRequest){
        smsService.checkCode(smsRequest);
        return Result.success("校验成功");
    }
}
