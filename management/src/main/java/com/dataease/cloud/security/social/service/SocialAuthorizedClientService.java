package com.dataease.cloud.security.social.service;

import com.dataease.cloud.base.domain.CloudSocialAuthorizedTokenKey;
import com.dataease.cloud.base.domain.CloudSocialAuthorizedTokenWithBLOBs;
import com.dataease.cloud.base.mapper.CloudSocialAuthorizedTokenMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * add Custom OAuth2 auth client service
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/6/1 5:02 PM
 **/
public class SocialAuthorizedClientService implements OAuth2AuthorizedClientService {

    private final CloudSocialAuthorizedTokenMapper tokenMapper;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public SocialAuthorizedClientService(CloudSocialAuthorizedTokenMapper tokenMapper, ClientRegistrationRepository clientRegistrationRepository) {
        this.tokenMapper = tokenMapper;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    public OAuth2AuthorizedClient loadAuthorizedClient(String clientRegistrationId, String principalName) {
        Assert.hasText(clientRegistrationId, "clientRegistrationId cannot be empty");
        Assert.hasText(principalName, "principalName cannot be empty");
        CloudSocialAuthorizedTokenKey key = new CloudSocialAuthorizedTokenKey();
        key.setClientRegistrationId(clientRegistrationId);
        key.setPrincipalName(principalName);
        CloudSocialAuthorizedTokenWithBLOBs clientWithBLOBs = tokenMapper.selectByPrimaryKey(key);
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
        if (clientRegistration == null || clientWithBLOBs == null) {
            return null;
        }
        Set<String> scopeSet = null;
        if (StringUtils.hasText(clientWithBLOBs.getAccessTokenScopes())) {
            scopeSet = new LinkedHashSet(Arrays.asList(StringUtils.delimitedListToStringArray(clientWithBLOBs.getAccessTokenScopes(), ",")));
        }
        OAuth2AccessToken accessToken = new OAuth2AccessToken(tokenType(), clientWithBLOBs.getAccessTokenValue()
                , toInstance(clientWithBLOBs.getAccessTokenIssuedAt()), toInstance(clientWithBLOBs.getAccessTokenExpiresAt()), scopeSet);
        // 一般情况下都没有这个
        if (StringUtils.hasText(clientWithBLOBs.getRefreshTokenValue())) {
            OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(clientWithBLOBs.getRefreshTokenValue(), toInstance(clientWithBLOBs.getRefreshTokenIssuedAt()), toInstance(clientWithBLOBs.getRefreshTokenExpiresAt()));
            return new OAuth2AuthorizedClient(clientRegistration, principalName, accessToken, refreshToken);
        } else {
            return new OAuth2AuthorizedClient(clientRegistration, principalName, accessToken);
        }
    }

    OAuth2AccessToken.TokenType tokenType() {
        return OAuth2AccessToken.TokenType.BEARER;
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        Assert.notNull(authorizedClient, "authorizedClient cannot be null");
        Assert.notNull(principal, "principal cannot be null");
        boolean existsAuthorizedClient = null != this.loadAuthorizedClient(
                authorizedClient.getClientRegistration().getRegistrationId(), principal.getName());
        if (existsAuthorizedClient) {
            updateAuthorizedClient(authorizedClient, principal);
        } else {
            insertAuthorizedClient(authorizedClient, principal);
        }
    }

    Instant toInstance(Date time) {
        if (time == null)
            return null;
        return time.toInstant();
    }

    Date toDate(Instant instant) {
        if (instant == null)
            return null;
        return Date.from(instant);
    }

    /**
     * update client token
     *
     * @param authorizedClient
     * @param principal
     */
    private void updateAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        CloudSocialAuthorizedTokenWithBLOBs clientWithBLOBs = new CloudSocialAuthorizedTokenWithBLOBs();
        clientWithBLOBs.setClientRegistrationId(authorizedClient.getClientRegistration().getRegistrationId());
        clientWithBLOBs.setPrincipalName(principal.getName());
        if (authorizedClient.getAccessToken() != null) {
            if (authorizedClient.getAccessToken().getTokenType() != null) {
                clientWithBLOBs.setAccessTokenType(authorizedClient.getAccessToken().getTokenType().getValue());
            }
            clientWithBLOBs.setAccessTokenValue(authorizedClient.getAccessToken().getTokenValue());
            clientWithBLOBs.setAccessTokenIssuedAt(toDate(authorizedClient.getAccessToken().getIssuedAt()));
            clientWithBLOBs.setAccessTokenExpiresAt(toDate(authorizedClient.getAccessToken().getExpiresAt()));
            if (!CollectionUtils.isEmpty(authorizedClient.getAccessToken().getScopes())) {
                clientWithBLOBs.setAccessTokenScopes(StringUtils.collectionToDelimitedString(authorizedClient.getAccessToken().getScopes(), ","));
            }
        }
        if (authorizedClient.getRefreshToken() != null) {
            clientWithBLOBs.setRefreshTokenValue(authorizedClient.getRefreshToken().getTokenValue());
            clientWithBLOBs.setRefreshTokenIssuedAt(toDate(authorizedClient.getRefreshToken().getIssuedAt()));
            clientWithBLOBs.setRefreshTokenExpiresAt(toDate(authorizedClient.getRefreshToken().getExpiresAt()));
        }
        // 更新有的字段
        tokenMapper.updateByPrimaryKeySelective(clientWithBLOBs);
    }

    void insertAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        CloudSocialAuthorizedTokenWithBLOBs clientWithBLOBs = new CloudSocialAuthorizedTokenWithBLOBs();
        clientWithBLOBs.setClientRegistrationId(authorizedClient.getClientRegistration().getRegistrationId());
        clientWithBLOBs.setPrincipalName(principal.getName());
        if (authorizedClient.getAccessToken() != null) {
            if (authorizedClient.getAccessToken().getTokenType() != null) {
                clientWithBLOBs.setAccessTokenType(authorizedClient.getAccessToken().getTokenType().getValue());
            }
            clientWithBLOBs.setAccessTokenValue(authorizedClient.getAccessToken().getTokenValue());
            clientWithBLOBs.setAccessTokenIssuedAt(toDate(authorizedClient.getAccessToken().getIssuedAt()));
            clientWithBLOBs.setAccessTokenExpiresAt(toDate(authorizedClient.getAccessToken().getExpiresAt()));
            if (!CollectionUtils.isEmpty(authorizedClient.getAccessToken().getScopes())) {
                clientWithBLOBs.setAccessTokenScopes(StringUtils.collectionToDelimitedString(authorizedClient.getAccessToken().getScopes(), ","));
            }
        }
        if (authorizedClient.getRefreshToken() != null) {
            clientWithBLOBs.setRefreshTokenValue(authorizedClient.getRefreshToken().getTokenValue());
            clientWithBLOBs.setRefreshTokenIssuedAt(toDate(authorizedClient.getRefreshToken().getIssuedAt()));
            clientWithBLOBs.setRefreshTokenExpiresAt(toDate(authorizedClient.getRefreshToken().getExpiresAt()));
        }
        tokenMapper.insertSelective(clientWithBLOBs);
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
        Assert.hasText(clientRegistrationId, "clientRegistrationId cannot be empty");
        Assert.hasText(principalName, "principalName cannot be empty");
        CloudSocialAuthorizedTokenKey key = new CloudSocialAuthorizedTokenKey();
        key.setClientRegistrationId(clientRegistrationId);
        key.setPrincipalName(principalName);
        tokenMapper.deleteByPrimaryKey(key);
    }
}
