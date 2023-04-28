CREATE TABLE cloud_sys_oper_log
(
    `oper_id`       bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
    `oper_title`    varchar(255) NULL COMMENT '操作描述',
    `oper_url`      varchar(255) NULL COMMENT '请求 URL',
    `request_type`  varchar(10) NULL COMMENT '请求方式',
    `business_type` varchar(128) NULL COMMENT '业务类型（select, increase, update, delete）',
    `oper_name`     varchar(50) NULL COMMENT '操作人员',
    `oper_ip`       varchar(128) NULL COMMENT '主机地址',
    `oper_location` varchar(255) NULL COMMENT '操作地点',
    `error_msg`     varchar(2000) NULL COMMENT '错误消息',
    `status`        varchar(50) NULL COMMENT '操作状态（normal, error）',
    `oper_time`     datetime(0) NULL COMMENT '操作时间',
    PRIMARY KEY (`oper_id`)
) engine=innodb auto_increment=100 comment = '操作日志记录';