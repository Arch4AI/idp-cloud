package com.dataease.cloud.base.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
* 表说明:
*  oauth授权条目
*
* 表名:cloud_oauth2_authorization
* Date: 2023/06/21
*/
@Data
public class CloudOauth2Authorization implements Serializable {
    /**
    * 字段说明:
    * ID
    *
    * 对应表中的列: cloud_oauth2_authorization.id
    *
    */
    private String id;

    /**
    * 字段说明:
    * 注册的客户端ID
    *
    * 对应表中的列: cloud_oauth2_authorization.registered_client_id
    *
    */
    private String registeredClientId;

    /**
    * 字段说明:
    * 用户ID
    *
    * 对应表中的列: cloud_oauth2_authorization.user_id
    *
    */
    private String userId;

    /**
    * 字段说明:
    * 授权类型
    *
    * 对应表中的列: cloud_oauth2_authorization.authorization_grant_type
    *
    */
    private String authorizationGrantType;

    /**
    * 字段说明:
    * 授权作用域逗号隔开
    *
    * 对应表中的列: cloud_oauth2_authorization.authorized_scopes
    *
    */
    private String authorizedScopes;

    /**
    * 字段说明:
    * 状态
    *
    * 对应表中的列: cloud_oauth2_authorization.state
    *
    */
    private String state;

    /**
    * 字段说明:
    * code创建时间
    *
    * 对应表中的列: cloud_oauth2_authorization.authorization_code_issued_at
    *
    */
    private Date authorizationCodeIssuedAt;

    /**
    * 字段说明:
    * code到期时间
    *
    * 对应表中的列: cloud_oauth2_authorization.authorization_code_expires_at
    *
    */
    private Date authorizationCodeExpiresAt;

    /**
    * 字段说明:
    * token创建时间
    *
    * 对应表中的列: cloud_oauth2_authorization.access_token_issued_at
    *
    */
    private Date accessTokenIssuedAt;

    /**
    * 字段说明:
    * token到期时间
    *
    * 对应表中的列: cloud_oauth2_authorization.access_token_expires_at
    *
    */
    private Date accessTokenExpiresAt;

    /**
    * 字段说明:
    * token类型
    *
    * 对应表中的列: cloud_oauth2_authorization.access_token_type
    *
    */
    private String accessTokenType;

    /**
    * 字段说明:
    * token作用域逗号隔开
    *
    * 对应表中的列: cloud_oauth2_authorization.access_token_scopes
    *
    */
    private String accessTokenScopes;

    /**
    * 字段说明:
    * id-token创建时间
    *
    * 对应表中的列: cloud_oauth2_authorization.oidc_id_token_issued_at
    *
    */
    private Date oidcIdTokenIssuedAt;

    /**
    * 字段说明:
    * id-token到期时间
    *
    * 对应表中的列: cloud_oauth2_authorization.oidc_id_token_expires_at
    *
    */
    private Date oidcIdTokenExpiresAt;

    /**
    * 字段说明:
    * refresh-token创建时间
    *
    * 对应表中的列: cloud_oauth2_authorization.refresh_token_issued_at
    *
    */
    private Date refreshTokenIssuedAt;

    /**
    * 字段说明:
    * refresh-token到期时间
    *
    * 对应表中的列: cloud_oauth2_authorization.refresh_token_expires_at
    *
    */
    private Date refreshTokenExpiresAt;

    /**
    * 字段说明:
    * session_id
    *
    * 对应表中的列: cloud_oauth2_authorization.session_id
    *
    */
    private String sessionId;

    private static final long serialVersionUID = 1L;
}
