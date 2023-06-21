package com.dataease.cloud.base.domain;

import java.io.Serializable;
import lombok.Data;

/**
* 大字段实体
* 表说明:
*  社交登录授权Token
* 
* 表名:cloud_social_authorized_token
* Date: 2023/06/21
*/
@Data
public class CloudSocialAuthorizedTokenWithBLOBs extends CloudSocialAuthorizedToken implements Serializable {
    /**
    * 字段说明:
    * 
    * 
    * 对应表中的列: cloud_social_authorized_token.access_token_value
    * 
    */ 
    private String accessTokenValue;

    /**
    * 字段说明:
    * 
    * 
    * 对应表中的列: cloud_social_authorized_token.refresh_token_value
    * 
    */ 
    private String refreshTokenValue;

    private static final long serialVersionUID = 1L;
}