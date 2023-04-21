package com.dataease.cloud.controller;

import com.dataease.cloud.entity.response.Result;
import com.dataease.cloud.i18n.I18nService;
import com.dataease.cloud.security.LocalUser;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/7 1:43 PM
 **/
@RestController
@RequestMapping("user")
public class UserController {

    /**
     * 返回个人信息
     *
     * @return
     */
    @GetMapping("me")
    public Result me(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            LocalUser localUser = (LocalUser) authentication.getPrincipal();
            localUser.setPassword(null);
            return Result.success(localUser);
        }
        return Result.error(I18nService.getMessage("user_not_login"));
    }
}
