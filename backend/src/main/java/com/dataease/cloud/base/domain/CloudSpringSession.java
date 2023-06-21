package com.dataease.cloud.base.domain;

import java.io.Serializable;
import lombok.Data;

/**
* 表说明:
*  
* 
* 表名:cloud_spring_session
* Date: 2023/06/21
*/
@Data
public class CloudSpringSession implements Serializable {
    /**
    * 字段说明:
    * 
    * 
    * 对应表中的列: cloud_spring_session.primary_id
    * 
    */ 
    private String primaryId;

    /**
    * 字段说明:
    * 
    * 
    * 对应表中的列: cloud_spring_session.session_id
    * 
    */ 
    private String sessionId;

    /**
    * 字段说明:
    * 
    * 
    * 对应表中的列: cloud_spring_session.creation_time
    * 
    */ 
    private Long creationTime;

    /**
    * 字段说明:
    * 
    * 
    * 对应表中的列: cloud_spring_session.last_access_time
    * 
    */ 
    private Long lastAccessTime;

    /**
    * 字段说明:
    * 
    * 
    * 对应表中的列: cloud_spring_session.max_inactive_interval
    * 
    */ 
    private Integer maxInactiveInterval;

    /**
    * 字段说明:
    * 
    * 
    * 对应表中的列: cloud_spring_session.expiry_time
    * 
    */ 
    private Long expiryTime;

    /**
    * 字段说明:
    * 
    * 
    * 对应表中的列: cloud_spring_session.principal_name
    * 
    */ 
    private String principalName;

    private static final long serialVersionUID = 1L;
}