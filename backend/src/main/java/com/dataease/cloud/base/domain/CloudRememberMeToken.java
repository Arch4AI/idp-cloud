package com.dataease.cloud.base.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
* 表说明:
*  RememberMe-Token持久化
*
* 表名:cloud_remember_me_token
* Date: 2023/06/21
*/
@Data
public class CloudRememberMeToken implements Serializable {
    /**
    * 字段说明:
    * 系列
    *
    * 对应表中的列: cloud_remember_me_token.series
    *
    */
    private String series;

    /**
    * 字段说明:
    * 用户ID
    *
    * 对应表中的列: cloud_remember_me_token.user_id
    *
    */
    private String userId;

    /**
    * 字段说明:
    * token
    *
    * 对应表中的列: cloud_remember_me_token.token
    *
    */
    private String token;

    /**
    * 字段说明:
    * 会话ID
    *
    * 对应表中的列: cloud_remember_me_token.session_id
    *
    */
    private String sessionId;

    /**
    * 字段说明:
    * 最后更新时间
    *
    * 对应表中的列: cloud_remember_me_token.last_used
    *
    */
    private Date lastUsed;

    private static final long serialVersionUID = 1L;
}
