package com.dataease.cloud.security.profile;

import com.dataease.cloud.i18n.I18nService;
import com.dataease.cloud.security.exception.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 用户更信息
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/8 9:54 AM
 **/
@Slf4j
public class ProfileAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    public ProfileAuthenticationProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //强制转换
        ProfileAuthenticationToken proflieAuthenticationToken = (ProfileAuthenticationToken) authentication;
        if (proflieAuthenticationToken.getPrincipal() == null)
            SecurityException.throwException(I18nService.getMessage("request_param_error"));
        String username = (String) proflieAuthenticationToken.getPrincipal();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        //设置token
        ProfileAuthenticationToken token = new ProfileAuthenticationToken(userDetails, userDetails.getAuthorities());
        token.setDetails(authentication.getDetails());
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ProfileAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
