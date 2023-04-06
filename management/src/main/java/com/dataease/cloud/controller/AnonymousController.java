package com.dataease.cloud.controller;

import com.dataease.cloud.entity.response.Result;
import com.dataease.cloud.service.CloudUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 匿名访问
 * 这个仅仅是测试请勿直接使用
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/8 4:20 PM
 **/
@RestController
@RequestMapping("anonymous")
@Slf4j
public class AnonymousController {

//    @Resource
//    private SessionRegistry sessionRegistry;

//    @Resource
//    private RedisSessionRepository sessionRepository;

    @Resource
    private CloudUserService cloudUserService;

    @GetMapping("sessions")
    public Result sessions() {
//        List<Object> principals = sessionRegistry.getAllPrincipals();
//        principals.stream().forEach(p->{
//            // 一个用户可能有几个session
//            List<SessionInformation> sessions = sessionRegistry.getAllSessions(p,false);
//            //打印一下
//            log.info("sessions:{}", JSON.toJSONString(sessions));
//        });
//        Set<String> keys = sessionRepository.getSessionRedisOperations().keys(RedisSessionRepository.DEFAULT_KEY_NAMESPACE + ":sessions*");
//        keys.forEach(key->{
//            Map<String, Object> entries = sessionRepository.getSessionRedisOperations().<String,Object>opsForHash().entries(key);
//            log.info(JSON.toJSONString(entries));
//        });
        return Result.success("666");
    }
}
