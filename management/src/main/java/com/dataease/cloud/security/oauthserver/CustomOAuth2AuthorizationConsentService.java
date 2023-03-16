package com.dataease.cloud.security.oauthserver;

import com.dataease.cloud.base.domain.CloudOauth2AuthorizationConsent;
import com.dataease.cloud.base.domain.CloudOauth2AuthorizationConsentKey;
import com.dataease.cloud.base.mapper.CloudOauth2AuthorizationConsentMapper;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * 自定义的服务
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/15 10:22 AM
 **/
public class CustomOAuth2AuthorizationConsentService implements OAuth2AuthorizationConsentService {

    private CloudOauth2AuthorizationConsentMapper cloudOauth2AuthorizationConsentMapper;
    private RegisteredClientRepository registeredClientRepository;

    public CustomOAuth2AuthorizationConsentService(CloudOauth2AuthorizationConsentMapper cloudOauth2AuthorizationConsentMapper,RegisteredClientRepository registeredClientRepository){
        this.cloudOauth2AuthorizationConsentMapper = cloudOauth2AuthorizationConsentMapper;
        this.registeredClientRepository = registeredClientRepository;
    }


    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
        Set<String> authorities = new HashSet<>();
        for (GrantedAuthority authority : authorizationConsent.getAuthorities()) {
            authorities.add(authority.getAuthority());
        }
        CloudOauth2AuthorizationConsent cloudOauth2AuthorizationConsent = new CloudOauth2AuthorizationConsent();
        cloudOauth2AuthorizationConsent.setAuthorities(StringUtils.collectionToDelimitedString(authorities, ","));
        cloudOauth2AuthorizationConsent.setPrincipalName(authorizationConsent.getPrincipalName());
        cloudOauth2AuthorizationConsent.setRegisteredClientId(authorizationConsent.getRegisteredClientId());
        //判断是不是存在
        CloudOauth2AuthorizationConsentKey key = new CloudOauth2AuthorizationConsentKey();
        key.setPrincipalName(authorizationConsent.getPrincipalName());
        key.setRegisteredClientId(authorizationConsent.getRegisteredClientId());
        CloudOauth2AuthorizationConsent consent = cloudOauth2AuthorizationConsentMapper.selectByPrimaryKey(key);
        if(consent == null){
            cloudOauth2AuthorizationConsentMapper.insertSelective(cloudOauth2AuthorizationConsent);
        } else{
            cloudOauth2AuthorizationConsentMapper.updateByPrimaryKeySelective(cloudOauth2AuthorizationConsent);
        }
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
        CloudOauth2AuthorizationConsentKey key = new CloudOauth2AuthorizationConsentKey();
        key.setPrincipalName(authorizationConsent.getPrincipalName());
        key.setRegisteredClientId(authorizationConsent.getRegisteredClientId());
        cloudOauth2AuthorizationConsentMapper.deleteByPrimaryKey(key);
    }

    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        Assert.hasText(registeredClientId, "registeredClientId cannot be empty");
        Assert.hasText(principalName, "principalName cannot be empty");
        CloudOauth2AuthorizationConsentKey key = new CloudOauth2AuthorizationConsentKey();
        key.setPrincipalName(principalName);
        key.setRegisteredClientId(registeredClientId);
        CloudOauth2AuthorizationConsent consent = cloudOauth2AuthorizationConsentMapper.selectByPrimaryKey(key);
        if(consent != null)
            return entityRow(consent);
        return null;
    }

    private OAuth2AuthorizationConsent entityRow(CloudOauth2AuthorizationConsent consent){
        String registeredClientId = consent.getRegisteredClientId();
        RegisteredClient registeredClient = this.registeredClientRepository.findById(registeredClientId);
        if (registeredClient == null) {
            throw new DataRetrievalFailureException(
                    "The RegisteredClient with id '" + registeredClientId + "' was not found in the RegisteredClientRepository.");
        }

        String principalName = consent.getPrincipalName();

        OAuth2AuthorizationConsent.Builder builder = OAuth2AuthorizationConsent.withId(registeredClientId, principalName);
        String authorizationConsentAuthorities = consent.getAuthorities();
        if (authorizationConsentAuthorities != null) {
            for (String authority : StringUtils.commaDelimitedListToSet(authorizationConsentAuthorities)) {
                builder.authority(new SimpleGrantedAuthority(authority));
            }
        }
        return builder.build();
    }
}
