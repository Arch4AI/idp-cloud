package com.dataease.cloud.security.oauthserver;

import com.dataease.cloud.base.domain.CloudOauth2RegisteredClient;
import com.dataease.cloud.base.domain.CloudOauth2RegisteredClientExample;
import com.dataease.cloud.base.mapper.CloudOauth2RegisteredClientMapper;
import com.dataease.cloud.utils.ObjectMapperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 自定义的处理器
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/15 10:20 AM
 **/
public class CustomRegisteredClientRepository implements RegisteredClientRepository {

    private static final ObjectMapper objectMapper = ObjectMapperUtils.objectMapper;

    private CloudOauth2RegisteredClientMapper cloudOauth2RegisteredClientMapper;

    public CustomRegisteredClientRepository(CloudOauth2RegisteredClientMapper cloudOauth2RegisteredClientMapper) {
        this.cloudOauth2RegisteredClientMapper = cloudOauth2RegisteredClientMapper;
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        Assert.notNull(registeredClient, "registeredClient cannot be null");
        List<String> clientAuthenticationMethods = registeredClient.getClientAuthenticationMethods()
                .stream().map(clientAuthenticationMethod -> clientAuthenticationMethod.getValue()).collect(Collectors.toList());
        List<String> authorizationGrantTypes = registeredClient.getAuthorizationGrantTypes().stream().map(authorizationGrantType -> authorizationGrantType.getValue()).collect(Collectors.toList());
        CloudOauth2RegisteredClient client = new CloudOauth2RegisteredClient();
        client.setId(registeredClient.getId());
        client.setClientId(registeredClient.getClientId());
        client.setClientSecret(registeredClient.getClientSecret());
        client.setClientIdIssuedAt(registeredClient.getClientIdIssuedAt() != null ? Date.from(registeredClient.getClientIdIssuedAt()) : null);
        client.setClientSecretExpiresAt(registeredClient.getClientSecretExpiresAt() != null ? Date.from(registeredClient.getClientSecretExpiresAt()) : null);
        client.setClientName(registeredClient.getClientName());
        client.setClientAuthenticationMethods(StringUtils.collectionToCommaDelimitedString(clientAuthenticationMethods));
        client.setAuthorizationGrantTypes(StringUtils.collectionToCommaDelimitedString(authorizationGrantTypes));
        client.setRedirectUris(StringUtils.collectionToCommaDelimitedString(registeredClient.getRedirectUris()));
        client.setScopes(StringUtils.collectionToCommaDelimitedString(registeredClient.getScopes()));
        client.setRequireProofKey(registeredClient.getClientSettings().isRequireProofKey());
        client.setRequireAuthorizationConsent(registeredClient.getClientSettings().isRequireAuthorizationConsent());
        client.setAuthorizationCodeTimeToLive((int) registeredClient.getTokenSettings().getAuthorizationCodeTimeToLive().toMinutes());
        client.setAccessTokenTimeToLive((int) registeredClient.getTokenSettings().getAccessTokenTimeToLive().toMinutes());
        client.setAccessTokenFormat(registeredClient.getTokenSettings().getAccessTokenFormat().getValue());
        client.setReuseRefreshTokens(registeredClient.getTokenSettings().isReuseRefreshTokens());
        client.setRefreshTokenTimeToLive((int) registeredClient.getTokenSettings().getAccessTokenTimeToLive().toMinutes());
        client.setIdTokenSignatureAlgorithm(registeredClient.getTokenSettings().getIdTokenSignatureAlgorithm().getName());
        //判断是不是已经存在了 存在了就更新 否则新增
        CloudOauth2RegisteredClient existingClient = cloudOauth2RegisteredClientMapper.selectByPrimaryKey(registeredClient.getId());
        if (existingClient == null) {
            assertUniqueIdentifiers(registeredClient);
            cloudOauth2RegisteredClientMapper.insertSelective(client);
        } else {
            // 直接更新 这里也是很奇怪 为什么不校验 是不是已经存在了clientId和clientSecret呢 因为这2个默认不能修改的
            cloudOauth2RegisteredClientMapper.updateByPrimaryKeySelective(client);
        }
    }

    /**
     * 校验一下
     *
     * @param registeredClient
     */
    private void assertUniqueIdentifiers(RegisteredClient registeredClient) {
        CloudOauth2RegisteredClientExample example = new CloudOauth2RegisteredClientExample();
        example.createCriteria().andClientIdEqualTo(registeredClient.getClientId());
        long count = cloudOauth2RegisteredClientMapper.countByExample(example);
        if (count > 0) {
            throw new IllegalArgumentException("Registered client must be unique. " +
                    "Found duplicate client identifier: " + registeredClient.getClientId());
        }
        example.clear();
        example.createCriteria().andClientSecretEqualTo(registeredClient.getClientSecret());
        count = cloudOauth2RegisteredClientMapper.countByExample(example);
        if (count > 0) {
            throw new IllegalArgumentException("Registered client must be unique. " +
                    "Found duplicate client secret for identifier: " + registeredClient.getId());
        }
    }

    @Override
    public RegisteredClient findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        CloudOauth2RegisteredClient client = cloudOauth2RegisteredClientMapper.selectByPrimaryKey(id);
        if (client != null) {
            return entityRow(client);
        }
        return null;
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        Assert.hasText(clientId, "clientId cannot be empty");
        CloudOauth2RegisteredClientExample example = new CloudOauth2RegisteredClientExample();
        example.createCriteria().andClientIdEqualTo(clientId);
        List<CloudOauth2RegisteredClient> clientList = cloudOauth2RegisteredClientMapper.selectByExample(example);
        if (!clientList.isEmpty()) {
            return entityRow(clientList.get(0));
        }
        return null;
    }

    /**
     * @param client
     * @return
     */
    public RegisteredClient entityRow(CloudOauth2RegisteredClient client) {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        Set<String> clientAuthenticationMethods = StringUtils.commaDelimitedListToSet(client.getClientAuthenticationMethods());
        Set<String> authorizationGrantTypes = StringUtils.commaDelimitedListToSet(client.getAuthorizationGrantTypes());
        Set<String> redirectUris = StringUtils.commaDelimitedListToSet(client.getRedirectUris());
        Set<String> clientScopes = StringUtils.commaDelimitedListToSet(client.getScopes());
        RegisteredClient.Builder builder = RegisteredClient.withId(client.getId())
                .clientId(client.getClientId())
                .clientIdIssuedAt(client.getClientIdIssuedAt() != null ? client.getClientIdIssuedAt().toInstant() : null)
                .clientSecret(encoder.encode(client.getClientSecret()))
                .clientSecretExpiresAt(client.getClientSecretExpiresAt() != null ? client.getClientSecretExpiresAt().toInstant() : null)
                .clientName(client.getClientName())
                .clientAuthenticationMethods((authenticationMethods) ->
                        clientAuthenticationMethods.forEach(authenticationMethod ->
                                authenticationMethods.add(resolveClientAuthenticationMethod(authenticationMethod))))
                .authorizationGrantTypes((grantTypes) ->
                        authorizationGrantTypes.forEach(grantType ->
                                grantTypes.add(resolveAuthorizationGrantType(grantType))))
                .redirectUris((uris) -> uris.addAll(redirectUris))
                .scopes((scopes) -> scopes.addAll(clientScopes));
        builder.clientSettings(ClientSettings.builder()
                .requireAuthorizationConsent(client.getRequireAuthorizationConsent())
                .requireProofKey(client.getRequireProofKey())
                .build());
        OAuth2TokenFormat format = OAuth2TokenFormat.SELF_CONTAINED;
        if (client.getAccessTokenFormat().equals(OAuth2TokenFormat.REFERENCE.getValue()))
            format = OAuth2TokenFormat.REFERENCE;
        builder.tokenSettings(TokenSettings.builder()
                .authorizationCodeTimeToLive(Duration.ofMinutes(client.getAuthorizationCodeTimeToLive()))
                .accessTokenTimeToLive(Duration.ofMinutes(client.getAccessTokenTimeToLive()))
                .accessTokenFormat(format)
                .reuseRefreshTokens(client.getReuseRefreshTokens())
                .refreshTokenTimeToLive(Duration.ofMinutes(client.getRefreshTokenTimeToLive()))
                .idTokenSignatureAlgorithm(SignatureAlgorithm.from(client.getIdTokenSignatureAlgorithm()))
                .build());
        return builder.build();
    }

    private static AuthorizationGrantType resolveAuthorizationGrantType(String authorizationGrantType) {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        } else if (AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.CLIENT_CREDENTIALS;
        } else if (AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.REFRESH_TOKEN;
        }
        return new AuthorizationGrantType(authorizationGrantType);
    }

    private static ClientAuthenticationMethod resolveClientAuthenticationMethod(String clientAuthenticationMethod) {
        if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        } else if (ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_POST;
        } else if (ClientAuthenticationMethod.NONE.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.NONE;
        }
        return new ClientAuthenticationMethod(clientAuthenticationMethod);
    }
}
