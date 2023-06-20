package com.dataease.cloud.security.social.registration;

import com.dataease.cloud.base.domain.CloudSocialSetting;
import com.dataease.cloud.base.mapper.CloudSocialSettingMapper;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.util.StringUtils;

/**
 * 自定义的OAuth client registration查询
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/6/1 2:56 PM
 **/
public class SocialClientRegistrationRepository implements ClientRegistrationRepository {

    //这个是默认的回调的参数
    private final static String REDIRECT_URI="{baseUrl}/login/oauth2/code/{registrationId}";

    private final CloudSocialSettingMapper settingMapper;

    public SocialClientRegistrationRepository(CloudSocialSettingMapper settingMapper){
        this.settingMapper = settingMapper;
    }

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        CloudSocialSetting setting = settingMapper.selectByPrimaryKey(registrationId);
        if(setting == null || !setting.getEnable())
            return null;
        return ClientRegistration.withRegistrationId(registrationId)
                .clientName(setting.getClientName())
                .clientAuthenticationMethod(toClientAuthenticationMethod(setting.getClientAuthMethod()))
                .clientId(setting.getClientId())
                .clientSecret(setting.getClientSecret())
                .redirectUri(REDIRECT_URI)
                .scope(StringUtils.delimitedListToStringArray(setting.getScopes(), ","))//默认采用逗号隔开
                .authorizationGrantType(toAuthorizationGrantType(setting.getAuthGrantType()))
                .userInfoAuthenticationMethod(toUserInfoAuthenticationMethod(setting.getUserAuthMethod()))
                .authorizationUri(setting.getAuthUri())
                .tokenUri(setting.getTokenUri())
                .userInfoUri(setting.getUserInfoUri())
                .userNameAttributeName(setting.getUserUniqueAttr())
                .build();
    }


    /**
     * return OAuth2 client auth method
     * @param method
     * @return
     */
    ClientAuthenticationMethod toClientAuthenticationMethod(String method) {
        if (!StringUtils.hasText(method)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        }
        return new ClientAuthenticationMethod(method.toLowerCase());
    }

    /**
     * return OAuth2 cient auth grant type
     * @param grantType
     * @return
     */
    AuthorizationGrantType toAuthorizationGrantType(String grantType) {
        if (!StringUtils.hasText(grantType)) {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        }
        return new AuthorizationGrantType(grantType.toLowerCase());
    }

    /**
     * return OAuth2 client user info auth method
     * @param method
     * @return
     */
    AuthenticationMethod toUserInfoAuthenticationMethod(String method) {
        if (!StringUtils.hasText(method)) {
            return AuthenticationMethod.HEADER;
        }
        return new AuthenticationMethod(method.toLowerCase());
    }
}
