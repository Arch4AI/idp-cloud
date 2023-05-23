package com.dataease.cloud.constant;

/**
 * 日志操作类型
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/5/23 3:19 PM
**/
public enum LogOperationType {
    /**
     * 查询
     */
    QUERY,
    /**
     * 添加
     */
    ADD,
    /**
     * 修改
     */
    UPDATE,
    /**
     * 删除
     */
    DELETE,
    /**
     * 校验
     */
    VERIFY,
    /**
     * 发送短信
     */
    SEND_SMS,
    /**
     * 无
     */
    NONE
}
