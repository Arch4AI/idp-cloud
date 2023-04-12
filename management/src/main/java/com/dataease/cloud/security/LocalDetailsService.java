package com.dataease.cloud.security;

import com.dataease.cloud.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 本地的一个userDetailsService
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/7 4:54 PM
 **/
@Service
public class LocalDetailsService implements UserDetailsService {

    @Resource
    private UserService userService;

    public LocalDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return userService.getUserById(username);
        } catch (Exception e) {
            throw new UsernameNotFoundException(e.getLocalizedMessage());
        }
    }
}
