package com.dataease.cloud.security.phone;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 手机验证码的提供者
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/8 9:54 AM
 **/
@Slf4j
public class PhoneAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    public PhoneAuthenticationProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //强制转换
        PhoneAuthenticationToken phoneAuthenticationToken = (PhoneAuthenticationToken) authentication;
        //获取用户信息
        UserDetails userDetails = userDetailsService.loadUserByUsername((String) phoneAuthenticationToken.getPrincipal());
        if (userDetails == null)
            throw new UsernameNotFoundException("user not found!");
        if (!userDetails.isEnabled())
            throw new DisabledException("user disabled!");
        //设置token
        PhoneAuthenticationToken token = new PhoneAuthenticationToken(userDetails, userDetails.getAuthorities());
        token.setDetails(authentication.getDetails());
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PhoneAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
