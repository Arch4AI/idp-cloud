package com.dataease.cloud.base.domain;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
* 表说明:
*  系统操作日志
* 
* 表名:cloud_sys_oper_log
* Date: 2023/06/21
*/
@Data
public class CloudSysOperLog implements Serializable {
    /**
    * 字段说明:
    * 主键
    * 
    * 对应表中的列: cloud_sys_oper_log.id
    * 
    */ 
    private Long id;

    /**
    * 字段说明:
    * 标题
    * 
    * 对应表中的列: cloud_sys_oper_log.title
    * 
    */ 
    private String title;

    /**
    * 字段说明:
    * 操作类型
    * 
    * 对应表中的列: cloud_sys_oper_log.oper_type
    * 
    */ 
    private String operType;

    /**
    * 字段说明:
    * 方法名称
    * 
    * 对应表中的列: cloud_sys_oper_log.method
    * 
    */ 
    private String method;

    /**
    * 字段说明:
    * 请求方式
    * 
    * 对应表中的列: cloud_sys_oper_log.request_method
    * 
    */ 
    private String requestMethod;

    /**
    * 字段说明:
    * 操作人ID
    * 
    * 对应表中的列: cloud_sys_oper_log.oper_user_id
    * 
    */ 
    private String operUserId;

    /**
    * 字段说明:
    * 操作人ID
    * 
    * 对应表中的列: cloud_sys_oper_log.oper_user_name
    * 
    */ 
    private String operUserName;

    /**
    * 字段说明:
    * 请求地址
    * 
    * 对应表中的列: cloud_sys_oper_log.request_url
    * 
    */ 
    private String requestUrl;

    /**
    * 字段说明:
    * 操作IP
    * 
    * 对应表中的列: cloud_sys_oper_log.ip
    * 
    */ 
    private String ip;

    /**
    * 字段说明:
    * 操作地址
    * 
    * 对应表中的列: cloud_sys_oper_log.location
    * 
    */ 
    private String location;

    /**
    * 字段说明:
    * 操作状态
    * 
    * 对应表中的列: cloud_sys_oper_log.status
    * 
    */ 
    private String status;

    /**
    * 字段说明:
    * 操作时间
    * 
    * 对应表中的列: cloud_sys_oper_log.create_time
    * 
    */ 
    private Date createTime;

    private static final long serialVersionUID = 1L;
}