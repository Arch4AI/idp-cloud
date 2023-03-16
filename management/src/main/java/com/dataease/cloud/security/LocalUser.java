package com.dataease.cloud.security;

import com.dataease.cloud.base.domain.CloudUser;
import com.dataease.cloud.constant.LoginType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

/**
 *
 * 一般认为userId 一致 就是一个用户 就要把那个人挤出去？ 只适用于本地
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/7 5:09 PM
 **/
public class LocalUser extends CloudUser implements UserDetails {

    private static final long serialVersionUID = 8290119021151510760L;

    //角色这个地方需要注意
    private Set<SimpleGrantedAuthority> authorities;

    //默认是密码登录
    private LoginType loginType = LoginType.PASSWORD;

    public LoginType getLoginType() {
        return loginType;
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    @Override
    public Set<SimpleGrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<SimpleGrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String getUsername() {
        return this.getUserId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    @Override
    public boolean isAccountNonLocked() {
        return true;
    }


    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return getEnabled();
    }
}
