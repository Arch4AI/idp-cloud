package com.dataease.cloud.security.external;

import com.dataease.cloud.security.exception.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 外部认证
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/8 9:54 AM
 **/
@Slf4j
public class ExternalAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    public ExternalAuthenticationProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //强制转换
        ExternalAuthenticationToken externalAuthenticationToken = (ExternalAuthenticationToken) authentication;
        if (externalAuthenticationToken.getPrincipal() == null)
            SecurityException.throwException("External Auth Parameter error!");
        String userId = (String) externalAuthenticationToken.getPrincipal();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        //设置token
        ExternalAuthenticationToken token = new ExternalAuthenticationToken(userDetails, userDetails.getAuthorities());
        token.setDetails(authentication.getDetails());
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ExternalAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
