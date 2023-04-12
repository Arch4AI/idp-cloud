package com.dataease.cloud.security.rememberme;

import com.dataease.cloud.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 记住我查询用户信息
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/7 4:54 PM
 **/
@Service
public class RememberMeDetailsService implements UserDetailsService {

    @Resource
    private UserService userService;

    public RememberMeDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userService.getUserById(username);
    }
}
