package com.dataease.cloud.security;

import com.dataease.cloud.service.CloudUserService;
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
    private CloudUserService cloudUserService;

    public LocalDetailsService(CloudUserService cloudUserService) {
        this.cloudUserService = cloudUserService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return cloudUserService.getUserById(username);
        } catch (Exception e) {
            throw new UsernameNotFoundException(e.getLocalizedMessage());
        }
    }
}
