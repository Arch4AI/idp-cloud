package com.dataease.cloud.controller;

import com.dataease.cloud.annotation.SysLog;
import com.dataease.cloud.constant.LogOperationType;
import com.dataease.cloud.entity.response.Result;
import com.dataease.cloud.service.SocialAuthenticationService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/13 6:21 PM
 **/
@RestController
@RequestMapping("auth")
public class AuthController {

    @Resource
    private SocialAuthenticationService socialAuthenticationService;

    /**
     * 获取可用的提供商
     *
     * @return
     */
    @SysLog(title = "获取可用的提供商", type = LogOperationType.QUERY)
    @GetMapping("providers")
    public Result listActiveProviders() {
        return Result.success(socialAuthenticationService.listActive());
    }
}
