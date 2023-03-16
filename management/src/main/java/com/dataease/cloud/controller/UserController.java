package com.dataease.cloud.controller;

import com.dataease.cloud.entity.response.Result;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * 用户控制器
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/7 1:43 PM
 **/
@RestController
@RequestMapping("user")
public class UserController {

    /**
     * 返回个人信息
     * @return
     */
    @GetMapping("me")
    public Result me(Authentication authentication){
        if(authentication.isAuthenticated()){
            return Result.success(authentication.getPrincipal());
        }
        return Result.error("用户未登录");
    }
}
