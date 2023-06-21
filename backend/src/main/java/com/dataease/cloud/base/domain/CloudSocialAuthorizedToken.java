package com.dataease.cloud.base.domain;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
* 表说明:
*  社交登录授权Token
* 
* 表名:cloud_social_authorized_token
* Date: 2023/06/21
*/
@Data
public class CloudSocialAuthorizedToken extends CloudSocialAuthorizedTokenKey implements Serializable {
    /**
    * 字段说明:
    * 
    * 
    * 对应表中的列: cloud_social_authorized_token.access_token_type
    * 
    */ 
    private String accessTokenType;

    /**
    * 字段说明:
    * 
    * 
    * 对应表中的列: cloud_social_authorized_token.access_token_issued_at
    * 
    */ 
    private Date accessTokenIssuedAt;

    /**
    * 字段说明:
    * 
    * 
    * 对应表中的列: cloud_social_authorized_token.access_token_expires_at
    * 
    */ 
    private Date accessTokenExpiresAt;

    /**
    * 字段说明:
    * 
    * 
    * 对应表中的列: cloud_social_authorized_token.access_token_scopes
    * 
    */ 
    private String accessTokenScopes;

    /**
    * 字段说明:
    * 
    * 
    * 对应表中的列: cloud_social_authorized_token.refresh_token_issued_at
    * 
    */ 
    private Date refreshTokenIssuedAt;

    /**
    * 字段说明:
    * 
    * 
    * 对应表中的列: cloud_social_authorized_token.refresh_token_expires_at
    * 
    */ 
    private Date refreshTokenExpiresAt;

    /**
    * 字段说明:
    * 
    * 
    * 对应表中的列: cloud_social_authorized_token.created_at
    * 
    */ 
    private Date createdAt;

    private static final long serialVersionUID = 1L;
}