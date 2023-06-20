package com.dataease.cloud.security.social.utils;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 帮助类
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/6/2 4:09 PM
 **/
public class SocialClientConfigurerUtils {

    public static <B extends HttpSecurityBuilder<B>> ClientRegistrationRepository getClientRegistrationRepository(B builder) {
        ClientRegistrationRepository clientRegistrationRepository = builder
                .getSharedObject(ClientRegistrationRepository.class);
        if (clientRegistrationRepository == null) {
            clientRegistrationRepository = getClientRegistrationRepositoryBean(builder);
            builder.setSharedObject(ClientRegistrationRepository.class, clientRegistrationRepository);
        }
        return clientRegistrationRepository;
    }

    private static <B extends HttpSecurityBuilder<B>> ClientRegistrationRepository getClientRegistrationRepositoryBean(
            B builder) {
        return builder.getSharedObject(ApplicationContext.class).getBean(ClientRegistrationRepository.class);
    }

    public static <B extends HttpSecurityBuilder<B>> OAuth2AuthorizedClientRepository getAuthorizedClientRepository(
            B builder) {
        OAuth2AuthorizedClientRepository authorizedClientRepository = builder
                .getSharedObject(OAuth2AuthorizedClientRepository.class);
        if (authorizedClientRepository == null) {
            authorizedClientRepository = getAuthorizedClientRepositoryBean(builder);
            if (authorizedClientRepository == null) {
                authorizedClientRepository = new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(
                        getAuthorizedClientService((builder)));
            }
            builder.setSharedObject(OAuth2AuthorizedClientRepository.class, authorizedClientRepository);
        }
        return authorizedClientRepository;
    }

    public static <B extends HttpSecurityBuilder<B>> OAuth2AccessTokenResponseClient getOAuth2AccessTokenResponseClient(B builder){
        OAuth2AccessTokenResponseClient oAuth2AccessTokenResponseClient = builder.getSharedObject(OAuth2AccessTokenResponseClient.class);
        if (oAuth2AccessTokenResponseClient == null) {
            oAuth2AccessTokenResponseClient = getOAuth2AccessTokenResponseClientBean(builder);
            if (oAuth2AccessTokenResponseClient == null) {
                oAuth2AccessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
            }
            builder.setSharedObject(OAuth2AccessTokenResponseClient.class, oAuth2AccessTokenResponseClient);
        }
        return oAuth2AccessTokenResponseClient;
    }

    private static <B extends HttpSecurityBuilder<B>> OAuth2AccessTokenResponseClient getOAuth2AccessTokenResponseClientBean(B builder){
        Map<String, OAuth2AccessTokenResponseClient> oAuth2AccessTokenResponseClientMap = BeanFactoryUtils
                .beansOfTypeIncludingAncestors(builder.getSharedObject(ApplicationContext.class),
                        OAuth2AccessTokenResponseClient.class);
        // 只能注册一个
        if (oAuth2AccessTokenResponseClientMap.size() > 1) {
            throw new NoUniqueBeanDefinitionException(OAuth2AuthorizedClientRepository.class,
                    oAuth2AccessTokenResponseClientMap.size(),
                    "Expected single matching bean of type '" + OAuth2AuthorizedClientRepository.class.getName()
                            + "' but found " + oAuth2AccessTokenResponseClientMap.size() + ": "
                            + StringUtils.collectionToCommaDelimitedString(oAuth2AccessTokenResponseClientMap.keySet()));
        }
        return (!oAuth2AccessTokenResponseClientMap.isEmpty() ? oAuth2AccessTokenResponseClientMap.values().iterator().next()
                : null);
    }

    private static <B extends HttpSecurityBuilder<B>> OAuth2AuthorizedClientRepository getAuthorizedClientRepositoryBean(
            B builder) {
        Map<String, OAuth2AuthorizedClientRepository> authorizedClientRepositoryMap = BeanFactoryUtils
                .beansOfTypeIncludingAncestors(builder.getSharedObject(ApplicationContext.class),
                        OAuth2AuthorizedClientRepository.class);
        if (authorizedClientRepositoryMap.size() > 1) {
            throw new NoUniqueBeanDefinitionException(OAuth2AuthorizedClientRepository.class,
                    authorizedClientRepositoryMap.size(),
                    "Expected single matching bean of type '" + OAuth2AuthorizedClientRepository.class.getName()
                            + "' but found " + authorizedClientRepositoryMap.size() + ": "
                            + StringUtils.collectionToCommaDelimitedString(authorizedClientRepositoryMap.keySet()));
        }
        return (!authorizedClientRepositoryMap.isEmpty() ? authorizedClientRepositoryMap.values().iterator().next()
                : null);
    }

    private static <B extends HttpSecurityBuilder<B>> OAuth2AuthorizedClientService getAuthorizedClientService(
            B builder) {
        OAuth2AuthorizedClientService authorizedClientService = getAuthorizedClientServiceBean(builder);
        if (authorizedClientService == null) {
            authorizedClientService = new InMemoryOAuth2AuthorizedClientService(
                    getClientRegistrationRepository(builder));
        }
        return authorizedClientService;
    }

    private static <B extends HttpSecurityBuilder<B>> OAuth2AuthorizedClientService getAuthorizedClientServiceBean(
            B builder) {
        Map<String, OAuth2AuthorizedClientService> authorizedClientServiceMap = BeanFactoryUtils
                .beansOfTypeIncludingAncestors(builder.getSharedObject(ApplicationContext.class),
                        OAuth2AuthorizedClientService.class);
        if (authorizedClientServiceMap.size() > 1) {
            throw new NoUniqueBeanDefinitionException(OAuth2AuthorizedClientService.class,
                    authorizedClientServiceMap.size(),
                    "Expected single matching bean of type '" + OAuth2AuthorizedClientService.class.getName()
                            + "' but found " + authorizedClientServiceMap.size() + ": "
                            + StringUtils.collectionToCommaDelimitedString(authorizedClientServiceMap.keySet()));
        }
        return (!authorizedClientServiceMap.isEmpty() ? authorizedClientServiceMap.values().iterator().next() : null);
    }
}
