package com.dataease.cloud.base.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
* 表说明:
*  oauth注册客户端
*
* 表名:cloud_oauth2_registered_client
* Date: 2023/06/21
*/
@Data
public class CloudOauth2RegisteredClient implements Serializable {
    /**
    * 字段说明:
    * 注册客户端ID
    *
    * 对应表中的列: cloud_oauth2_registered_client.id
    *
    */
    private String id;

    /**
    * 字段说明:
    * 客户端ID
    *
    * 对应表中的列: cloud_oauth2_registered_client.client_id
    *
    */
    private String clientId;

    /**
    * 字段说明:
    * 创建时间
    *
    * 对应表中的列: cloud_oauth2_registered_client.client_id_issued_at
    *
    */
    private Date clientIdIssuedAt;

    /**
    * 字段说明:
    * 客户端密钥
    *
    * 对应表中的列: cloud_oauth2_registered_client.client_secret
    *
    */
    private String clientSecret;

    /**
    * 字段说明:
    * 客户端到期时间
    *
    * 对应表中的列: cloud_oauth2_registered_client.client_secret_expires_at
    *
    */
    private Date clientSecretExpiresAt;

    /**
    * 字段说明:
    * 客户端名称
    *
    * 对应表中的列: cloud_oauth2_registered_client.client_name
    *
    */
    private String clientName;

    /**
    * 字段说明:
    * 认证方法逗号隔开
    *
    * 对应表中的列: cloud_oauth2_registered_client.client_authentication_methods
    *
    */
    private String clientAuthenticationMethods;

    /**
    * 字段说明:
    * 授权类型逗号隔开
    *
    * 对应表中的列: cloud_oauth2_registered_client.authorization_grant_types
    *
    */
    private String authorizationGrantTypes;

    /**
    * 字段说明:
    * 允许回调地址逗号隔开
    *
    * 对应表中的列: cloud_oauth2_registered_client.redirect_uris
    *
    */
    private String redirectUris;

    /**
    * 字段说明:
    * 作用域逗号隔开
    *
    * 对应表中的列: cloud_oauth2_registered_client.scopes
    *
    */
    private String scopes;

    /**
    * 字段说明:
    * 是否开启code_challenge校验
    *
    * 对应表中的列: cloud_oauth2_registered_client.require_proof_key
    *
    */
    private Boolean requireProofKey;

    /**
    * 字段说明:
    * 是否开启首次同意的校验
    *
    * 对应表中的列: cloud_oauth2_registered_client.require_authorization_consent
    *
    */
    private Boolean requireAuthorizationConsent;

    /**
    * 字段说明:
    * code的有效时间(分)
    *
    * 对应表中的列: cloud_oauth2_registered_client.authorization_code_time_to_live
    *
    */
    private Integer authorizationCodeTimeToLive;

    /**
    * 字段说明:
    * token的有效时间(分)
    *
    * 对应表中的列: cloud_oauth2_registered_client.access_token_time_to_live
    *
    */
    private Integer accessTokenTimeToLive;

    /**
    * 字段说明:
    * token的格式
    *
    * 对应表中的列: cloud_oauth2_registered_client.access_token_format
    *
    */
    private String accessTokenFormat;

    /**
    * 字段说明:
    * 是否重用refresh-token
    *
    * 对应表中的列: cloud_oauth2_registered_client.reuse_refresh_tokens
    *
    */
    private Boolean reuseRefreshTokens;

    /**
    * 字段说明:
    * 刷新token的有效时间(分)
    *
    * 对应表中的列: cloud_oauth2_registered_client.refresh_token_time_to_live
    *
    */
    private Integer refreshTokenTimeToLive;

    /**
    * 字段说明:
    * id-token加密方式
    *
    * 对应表中的列: cloud_oauth2_registered_client.id_token_signature_algorithm
    *
    */
    private String idTokenSignatureAlgorithm;

    private static final long serialVersionUID = 1L;
}
