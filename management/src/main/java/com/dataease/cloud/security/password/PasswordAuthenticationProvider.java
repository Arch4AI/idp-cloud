package com.dataease.cloud.security.password;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 校验密码是否正确
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/8 9:54 AM
 **/
@Slf4j
public class PasswordAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    //密码管理器
    private PasswordEncoder passwordEncoder;

    public PasswordAuthenticationProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        setPasswordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
    }

    public PasswordAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //强制转换
        String userName = "";
        if (authentication.getPrincipal() instanceof String) {
            userName = (String) authentication.getPrincipal();
        }
        String password = (String) authentication.getCredentials();//这个也是验证码
        //获取用户信息
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
        //判断是不是可以
        if (!this.passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Bad credentials!");
        }
        //设置token
        PasswordAuthenticationToken token = new PasswordAuthenticationToken(userDetails, userDetails.getAuthorities());
        token.setDetails(authentication.getDetails());
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
