package com.dataease.cloud.security.social.base;

import com.dataease.cloud.security.social.secret.AbstractSecret;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.Set;

/**
 * OAuth2.0 client need defind thing
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/6/2 10:29 AM
 **/
public interface ClientRegistrationSetting {

    /**
     * 授权的api
     *
     * @return url
     */
    String authorize();

    /**
     * 获取accessToken的api
     *
     * @return url
     */
    String accessToken();

    /**
     * 获取用户信息的api
     *
     * @return url
     */
    String userInfo();

    /**
     * 作用域
     */
    Set<String> scopes();

    /**
     * 作用域的分隔符
     *
     * @return
     */
    default String scopeSeparator() {
        return " ";
    }

    /**
     * 客户端认证方法
     *
     * @return
     */
    ClientAuthenticationMethod clientAuthenticationMethod();

    /**
     * 认证方法
     *
     * @return
     */
    AuthorizationGrantType authorizationGrantType();

    /**
     * 用户认证的方法
     *
     * @return
     */
    AuthenticationMethod userInfoAuthenticationMethod();

    /**
     * 用户属性
     *
     * @return
     */
    String userNameAttributeName();

    /**
     * 获取当前的名称
     *
     * @return name
     */
    default String getName() {
        if (this instanceof Enum) {
            return String.valueOf(this);
        }
        return this.getClass().getSimpleName();
    }

    Class<? extends AbstractSecret> getSecretClass();
}
