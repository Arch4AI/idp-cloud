DROP TABLE IF EXISTS `cloud_social_setting`;
CREATE TABLE `cloud_social_setting`
(
    `id`                 varchar(64)  NOT NULL COMMENT '主键',
    `client_name`        varchar(256) NOT NULL COMMENT '客户端名称',
    `icon`               varchar(256) NULL COMMENT '显示图标',
    `client_id`          varchar(256) NOT NULL COMMENT '客户端ID',
    `client_secret`      varchar(256) NOT NULL COMMENT '客户端密钥',
    `client_type`        varchar(32)  NOT NULL COMMENT '客服端类型',
    `client_auth_method` varchar(32) NULL COMMENT '客户端认证方法',
    `auth_grant_type`    varchar(64) NULL COMMENT '授权方法',
    `scopes`             varchar(256) NULL COMMENT '逗号隔开',
    `scope_separator`    varchar(16) NULL COMMENT '分隔符为空的时候默认就是空格',
    `auth_uri`           varchar(256) NULL COMMENT '认证地址',
    `token_uri`          varchar(256) NULL COMMENT 'token地址',
    `user_info_uri`      varchar(256) NULL COMMENT '用户信息地址',
    `user_unique_attr`   varchar(32) NULL COMMENT '用户唯一属性',
    `user_auth_method`   varchar(32) NULL COMMENT '用户信息认证方法',
    `auth_secret`        text         NOT NULL COMMENT '认证密钥',
    `enable`             boolean default true COMMENT '是否启用',
    `create_time`        timestamp NULL COMMENT '创建时间',
    `update_time`        timestamp NULL COMMENT '修改时间',
    PRIMARY KEY (`id`)
) COMMENT = '社交登录配置';

DROP TABLE IF EXISTS `cloud_social_authorized_token`;
CREATE TABLE `cloud_social_authorized_token`
(
    client_registration_id   varchar(64)                            NOT NULL,
    principal_name           varchar(256)                           NOT NULL,
    access_token_type        varchar(128)                           NOT NULL,
    access_token_value       text                                   NOT NULL,
    access_token_issued_at   timestamp                              NOT NULL,
    access_token_expires_at  timestamp                              NOT NULL,
    access_token_scopes      varchar(256) DEFAULT NULL,
    refresh_token_value      text         DEFAULT NULL,
    refresh_token_issued_at  timestamp    DEFAULT NULL,
    refresh_token_expires_at timestamp    DEFAULT NULL,
    created_at               timestamp    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (client_registration_id, principal_name)
) COMMENT = '社交登录授权Token';

DROP TABLE IF EXISTS `cloud_user_social`;
CREATE TABLE `cloud_user_social`
(
    `social_setting_id` varchar(64)  NOT NULL COMMENT '社交登录设置ID',
    `social_user_uuid`  varchar(255) NOT NULL COMMENT '社交用户UUID',
    `user_id`           varchar(64) NULL COMMENT '用户ID',
    `user_raw`          longtext NULL COMMENT '用户原始数据',
    `create_time`       datetime(0) NULL COMMENT '创建时间',
    `update_time`       datetime(0) NULL COMMENT '修改时间',
    PRIMARY KEY (`social_setting_id`, `social_user_uuid`),
    INDEX               `idx_user_id`(`user_id`) USING BTREE COMMENT '用户ID索引'
) COMMENT = '用户与社交登录的关联';
