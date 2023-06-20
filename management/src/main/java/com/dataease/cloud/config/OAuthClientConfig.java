package com.dataease.cloud.config;

import com.dataease.cloud.base.mapper.CloudSocialAuthorizedTokenMapper;
import com.dataease.cloud.base.mapper.CloudSocialSettingMapper;
import com.dataease.cloud.security.handler.CustomAuthenticationFailureHandler;
import com.dataease.cloud.security.handler.CustomSuccessHandler;
import com.dataease.cloud.security.social.configurer.SocialLoginConfigurer;
import com.dataease.cloud.security.social.endpoint.SocialAuthorizationCodeTokenResponseClient;
import com.dataease.cloud.security.social.filter.SocialAuthorizationRequestRedirectFilter;
import com.dataease.cloud.security.social.filter.SocialLoginAuthenticationFilter;
import com.dataease.cloud.security.social.registration.SocialClientRegistrationRepository;
import com.dataease.cloud.security.social.resolver.SocialAuthorizationRequestResolver;
import com.dataease.cloud.security.social.service.SocialAuthorizedClientService;
import com.dataease.cloud.security.social.userinfo.SocialUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;

/**
 * 配置客户端
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/6/1 4:52 PM
 **/
@Configuration(proxyBeanMethods = false)
@Slf4j
public class OAuthClientConfig {

    //请求回调URI
    private final static String OAUTH2_REQUEST_REDIRECT_URI = SocialAuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;

    //默认回调回来的URI
    private final static String OAUTH2_AUTHORIZATION_RESPONSE_BASE_URI = SocialLoginAuthenticationFilter.DEFAULT_FILTER_PROCESSES_URI;


    @Bean
    public SocialLoginConfigurer socialLoginConfigurer(CustomSuccessHandler successHandler, CustomAuthenticationFailureHandler failureHandler) {
        return new SocialLoginConfigurer(successHandler, failureHandler);
    }


    /**
     * add oauth auth client service
     *
     * @param tokenMapper
     * @param clientRegistrationRepository
     * @return
     */
    @Bean
    public OAuth2AuthorizedClientService oAuth2AuthorizedClientService(CloudSocialAuthorizedTokenMapper tokenMapper,
                                                                       ClientRegistrationRepository clientRegistrationRepository) {
        return new SocialAuthorizedClientService(tokenMapper, clientRegistrationRepository);
    }

    /**
     * add client Registration setting
     *
     * @param settingMapper
     * @return
     */
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(CloudSocialSettingMapper settingMapper) {
        return new SocialClientRegistrationRepository(settingMapper);
    }

    /**
     * custom request resolver
     *
     * @param registrationRepository
     * @return
     */
    @Bean
    public OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver(CloudSocialSettingMapper settingMapper, ClientRegistrationRepository registrationRepository) {
        return new SocialAuthorizationRequestResolver(registrationRepository, settingMapper, OAUTH2_REQUEST_REDIRECT_URI);
    }

    /**
     * custom authorization code token response client
     *
     * @param settingMapper
     * @return
     */
    @Bean
    public SocialAuthorizationCodeTokenResponseClient customAuthorizationCodeTokenResponseClient(CloudSocialSettingMapper settingMapper) {
        return new SocialAuthorizationCodeTokenResponseClient(settingMapper);
    }

    /**
     * add catch user info service
     *
     * @param settingMapper
     * @return
     */
    @Bean
    public OAuth2UserService socialUserService(CloudSocialSettingMapper settingMapper) {
        return new SocialUserService(settingMapper);
    }
}
