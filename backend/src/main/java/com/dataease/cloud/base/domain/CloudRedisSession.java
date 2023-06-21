package com.dataease.cloud.base.domain;

import java.io.Serializable;
import lombok.Data;

/**
* 表说明:
*  用户redis_session关联管理
* 
* 表名:cloud_redis_session
* Date: 2023/06/21
*/
@Data
public class CloudRedisSession implements Serializable {
    /**
    * 字段说明:
    * session_id
    * 
    * 对应表中的列: cloud_redis_session.session_id
    * 
    */ 
    private String sessionId;

    /**
    * 字段说明:
    * 用户ID
    * 
    * 对应表中的列: cloud_redis_session.user_id
    * 
    */ 
    private String userId;

    private static final long serialVersionUID = 1L;
}