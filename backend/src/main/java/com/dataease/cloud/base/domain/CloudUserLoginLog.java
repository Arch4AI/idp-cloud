package com.dataease.cloud.base.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
* 表说明:
*  用户登录日志
*
* 表名:cloud_user_login_log
* Date: 2023/06/21
*/
@Data
public class CloudUserLoginLog implements Serializable {
    /**
    * 字段说明:
    * 主键
    *
    * 对应表中的列: cloud_user_login_log.id
    *
    */
    private Long id;

    /**
    * 字段说明:
    * 用户ID
    *
    * 对应表中的列: cloud_user_login_log.user_id
    *
    */
    private String userId;

    /**
    * 字段说明:
    * 注册客户端ID;外部登录的
    *
    * 对应表中的列: cloud_user_login_log.registered_client_id
    *
    */
    private Long registeredClientId;

    /**
    * 字段说明:
    * sessionId;sessionId唯一
    *
    * 对应表中的列: cloud_user_login_log.session_id
    *
    */
    private String sessionId;

    /**
    * 字段说明:
    * IP地址
    *
    * 对应表中的列: cloud_user_login_log.ip
    *
    */
    private String ip;

    /**
    * 字段说明:
    * 登录地点
    *
    * 对应表中的列: cloud_user_login_log.login_location
    *
    */
    private String loginLocation;

    /**
    * 字段说明:
    * 浏览器类型
    *
    * 对应表中的列: cloud_user_login_log.browser
    *
    */
    private String browser;

    /**
    * 字段说明:
    * 操作系统
    *
    * 对应表中的列: cloud_user_login_log.os
    *
    */
    private String os;

    /**
    * 字段说明:
    * 登录状态（0失败 1成功）
    *
    * 对应表中的列: cloud_user_login_log.status
    *
    */
    private Integer status;

    /**
    * 字段说明:
    * 提示消息
    *
    * 对应表中的列: cloud_user_login_log.msg
    *
    */
    private String msg;

    /**
    * 字段说明:
    * 登录时间
    *
    * 对应表中的列: cloud_user_login_log.login_time
    *
    */
    private Date loginTime;

    private static final long serialVersionUID = 1L;
}
