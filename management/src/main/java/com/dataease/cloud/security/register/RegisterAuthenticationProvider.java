package com.dataease.cloud.security.register;

import com.dataease.cloud.security.exception.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 用户注册
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/8 9:54 AM
 **/
@Slf4j
public class RegisterAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    public RegisterAuthenticationProvider(UserDetailsService userDetailsService){
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //强制转换
        RegisterAuthenticationToken registerAuthenticationToken = (RegisterAuthenticationToken)authentication;
        if(registerAuthenticationToken.getPrincipal() == null)
            SecurityException.throwException("register request paramter error!");
        String username = (String)registerAuthenticationToken.getPrincipal();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        //设置token
        RegisterAuthenticationToken token = new RegisterAuthenticationToken(userDetails,userDetails.getAuthorities());
        token.setDetails(authentication.getDetails());
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return RegisterAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
