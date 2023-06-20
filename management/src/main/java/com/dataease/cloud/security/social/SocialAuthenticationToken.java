package com.dataease.cloud.security.social;

import com.dataease.cloud.security.LocalUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.util.Assert;

/**
 * 社交返回的信息
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/6/19 5:32 PM
 **/
public class SocialAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final LocalUser principal;
    private final String authorizedClientRegistrationId;
    private final Object auth2User;

    public SocialAuthenticationToken(LocalUser principal,
                                     Object auth2User,
                                     String authorizedClientRegistrationId) {
        super(principal.getAuthorities());
        Assert.notNull(principal, "principal cannot be null");
        this.principal = principal;
        this.auth2User = auth2User;
        this.authorizedClientRegistrationId = authorizedClientRegistrationId;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
