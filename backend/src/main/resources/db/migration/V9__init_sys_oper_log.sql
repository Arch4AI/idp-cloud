DROP TABLE IF EXISTS `cloud_sys_oper_log`;
CREATE TABLE cloud_sys_oper_log
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title`          varchar(256) NOT NULL COMMENT '标题',
    `oper_type`      varchar(128) NOT NULL COMMENT '操作类型',
    `method`         varchar(128) DEFAULT NULL COMMENT '方法名称',
    `request_method` varchar(10)  DEFAULT NULL COMMENT '请求方式',
    `oper_user_id`   varchar(50)  DEFAULT NULL COMMENT '操作人ID',
    `oper_user_name` varchar(50)  DEFAULT NULL COMMENT '操作人ID',
    `request_url`    varchar(256) DEFAULT NULL COMMENT '请求地址',
    `ip`             varchar(128) DEFAULT NULL COMMENT '操作IP',
    `location`       varchar(256) DEFAULT NULL COMMENT '操作地址',
    `request_param`  text         DEFAULT NULL COMMENT '请求参数',
    `reponse_body`   text         DEFAULT NULL COMMENT '响应体',
    `status`         varchar(8)   NOT NULL COMMENT '操作状态',
    `error_msg`      text         DEFAULT NULL COMMENT '错误消息',
    `create_time`    datetime     NOT NULL COMMENT '操作时间',
    PRIMARY KEY (`id`)
) COMMENT = '系统操作日志';
