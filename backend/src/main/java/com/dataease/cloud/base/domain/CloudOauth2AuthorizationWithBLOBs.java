package com.dataease.cloud.base.domain;

import java.io.Serializable;
import lombok.Data;

/**
* 大字段实体
* 表说明:
*  oauth授权条目
* 
* 表名:cloud_oauth2_authorization
* Date: 2023/06/21
*/
@Data
public class CloudOauth2AuthorizationWithBLOBs extends CloudOauth2Authorization implements Serializable {
    /**
    * 字段说明:
    * 登录会话信息
    * 
    * 对应表中的列: cloud_oauth2_authorization.attributes
    * 
    */ 
    private String attributes;

    /**
    * 字段说明:
    * 授权code
    * 
    * 对应表中的列: cloud_oauth2_authorization.authorization_code_value
    * 
    */ 
    private String authorizationCodeValue;

    /**
    * 字段说明:
    * code元数据
    * 
    * 对应表中的列: cloud_oauth2_authorization.authorization_code_metadata
    * 
    */ 
    private String authorizationCodeMetadata;

    /**
    * 字段说明:
    * token
    * 
    * 对应表中的列: cloud_oauth2_authorization.access_token_value
    * 
    */ 
    private String accessTokenValue;

    /**
    * 字段说明:
    * token元数据
    * 
    * 对应表中的列: cloud_oauth2_authorization.access_token_metadata
    * 
    */ 
    private String accessTokenMetadata;

    /**
    * 字段说明:
    * id-token
    * 
    * 对应表中的列: cloud_oauth2_authorization.oidc_id_token_value
    * 
    */ 
    private String oidcIdTokenValue;

    /**
    * 字段说明:
    * id-token元数据
    * 
    * 对应表中的列: cloud_oauth2_authorization.oidc_id_token_metadata
    * 
    */ 
    private String oidcIdTokenMetadata;

    /**
    * 字段说明:
    * refresh-token
    * 
    * 对应表中的列: cloud_oauth2_authorization.refresh_token_value
    * 
    */ 
    private String refreshTokenValue;

    /**
    * 字段说明:
    * refresh-token元数据
    * 
    * 对应表中的列: cloud_oauth2_authorization.refresh_token_metadata
    * 
    */ 
    private String refreshTokenMetadata;

    private static final long serialVersionUID = 1L;
}