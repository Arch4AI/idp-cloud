package com.dataease.cloud.controller;

import com.dataease.cloud.entity.response.Result;
import com.dataease.cloud.service.ExternalAuthSourceService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private ExternalAuthSourceService externalAuthSourceService;

    /**
     * 获取可用的提供商
     *
     * @return
     */
    @GetMapping("providers")
    public Result listActiveProviders() {
        return Result.success(externalAuthSourceService.listActive());
    }

    /**
     * 根据ID获取跳转路径
     *
     * @param id
     * @return
     */
    @GetMapping("providers/{id}")
    public Result redirectUri(@PathVariable("id") String id) {
        return Result.success(externalAuthSourceService.getSettingById(id));
    }
}
