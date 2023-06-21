package com.dataease.cloud.base.domain;

import java.io.Serializable;
import lombok.Data;

/**
* 大字段实体
* 表说明:
*  系统操作日志
* 
* 表名:cloud_sys_oper_log
* Date: 2023/06/21
*/
@Data
public class CloudSysOperLogWithBLOBs extends CloudSysOperLog implements Serializable {
    /**
    * 字段说明:
    * 请求参数
    * 
    * 对应表中的列: cloud_sys_oper_log.request_param
    * 
    */ 
    private String requestParam;

    /**
    * 字段说明:
    * 响应体
    * 
    * 对应表中的列: cloud_sys_oper_log.reponse_body
    * 
    */ 
    private String reponseBody;

    /**
    * 字段说明:
    * 错误消息
    * 
    * 对应表中的列: cloud_sys_oper_log.error_msg
    * 
    */ 
    private String errorMsg;

    private static final long serialVersionUID = 1L;
}