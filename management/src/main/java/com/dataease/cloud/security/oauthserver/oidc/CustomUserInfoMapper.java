package com.dataease.cloud.security.oauthserver.oidc;

import cn.hutool.core.bean.BeanUtil;
import com.dataease.cloud.security.LocalUser;
import com.dataease.cloud.service.UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;

import java.util.*;
import java.util.function.Function;

/**
 * 返回用户信息
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/16 9:39 AM
 **/
public class CustomUserInfoMapper implements Function<OidcUserInfoAuthenticationContext, OidcUserInfo> {

    private UserService userService;

    public CustomUserInfoMapper(UserService userService) {
        this.userService = userService;
    }

    private static final List<String> EMAIL_CLAIMS = Arrays.asList(
            CustomClaimNames.EMAIL
    );
    private static final List<String> PHONE_CLAIMS = Arrays.asList(
            CustomClaimNames.PHONE
    );
    private static final List<String> PROFILE_CLAIMS = Arrays.asList(
            CustomClaimNames.CREATE_TIME,
            CustomClaimNames.GENDER,
            CustomClaimNames.LANGUAGE,
            CustomClaimNames.NICKNAME,
            CustomClaimNames.UPDATE_TIME,
            CustomClaimNames.USERANME,
            CustomClaimNames.USERID
    );

    @Override
    public OidcUserInfo apply(OidcUserInfoAuthenticationContext authenticationContext) {
        OAuth2Authorization authorization = authenticationContext.getAuthorization();
        OidcIdToken idToken = authorization.getToken(OidcIdToken.class).getToken();
        OAuth2AccessToken accessToken = authenticationContext.getAccessToken();
        Map<String, Object> objectMap = idToken.getClaims();
        String userId = authorization.getPrincipalName();
        LocalUser localUser = userService.getUserById(userId);
        Map<String, Object> userMap = BeanUtil.beanToMap(localUser);
        userMap.putAll(objectMap);
        Map<String, Object> scopeRequestedClaims = getClaimsRequestedByScope(userMap,
                accessToken.getScopes());
        return new OidcUserInfo(scopeRequestedClaims);
    }

    private static Map<String, Object> getClaimsRequestedByScope(Map<String, Object> claims, Set<String> requestedScopes) {
        Set<String> scopeRequestedClaimNames = new HashSet<>();
        scopeRequestedClaimNames.add(StandardClaimNames.SUB);

        if (requestedScopes.contains(OidcScopes.EMAIL)) {
            scopeRequestedClaimNames.addAll(EMAIL_CLAIMS);
        }
        if (requestedScopes.contains(OidcScopes.PHONE)) {
            scopeRequestedClaimNames.addAll(PHONE_CLAIMS);
        }
        if (requestedScopes.contains(OidcScopes.PROFILE)) {
            scopeRequestedClaimNames.addAll(PROFILE_CLAIMS);
        }
        Map<String, Object> requestedClaims = new HashMap<>(claims);
        requestedClaims.keySet().removeIf(claimName -> !scopeRequestedClaimNames.contains(claimName));
        return requestedClaims;
    }
}
