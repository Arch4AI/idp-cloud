-- 登录的用户
DROP TABLE IF EXISTS `cloud_oauth2_authorization`;
CREATE TABLE `cloud_oauth2_authorization`
(
    `id`                            varchar(100) NOT NULL COMMENT 'ID',
    `registered_client_id`          varchar(100) NOT NULL COMMENT '注册的客户端ID',
    `user_id`                       varchar(200) NOT NULL COMMENT '用户ID',
    `authorization_grant_type`      varchar(100) NOT NULL COMMENT '授权类型',
    `authorized_scopes`             varchar(1000) DEFAULT NULL COMMENT '授权作用域逗号隔开',
    `attributes`                    text COMMENT '登录会话信息',
    `state`                         varchar(500)  DEFAULT NULL COMMENT '状态',
    `authorization_code_value`      text COMMENT '授权code',
    `authorization_code_issued_at`  timestamp NULL DEFAULT NULL COMMENT 'code创建时间',
    `authorization_code_expires_at` timestamp NULL DEFAULT NULL COMMENT 'code到期时间',
    `authorization_code_metadata`   text COMMENT 'code元数据',
    `access_token_value`            text COMMENT 'token',
    `access_token_issued_at`        timestamp NULL DEFAULT NULL COMMENT 'token创建时间',
    `access_token_expires_at`       timestamp NULL DEFAULT NULL COMMENT 'token到期时间',
    `access_token_metadata`         text COMMENT 'token元数据',
    `access_token_type`             varchar(100)  DEFAULT NULL COMMENT 'token类型',
    `access_token_scopes`           varchar(1000) DEFAULT NULL COMMENT 'token作用域逗号隔开',
    `oidc_id_token_value`           text COMMENT 'id-token',
    `oidc_id_token_issued_at`       timestamp NULL DEFAULT NULL COMMENT 'id-token创建时间',
    `oidc_id_token_expires_at`      timestamp NULL DEFAULT NULL COMMENT 'id-token到期时间',
    `oidc_id_token_metadata`        text COMMENT 'id-token元数据',
    `refresh_token_value`           text COMMENT 'refresh-token',
    `refresh_token_issued_at`       timestamp NULL DEFAULT NULL COMMENT 'refresh-token创建时间',
    `refresh_token_expires_at`      timestamp NULL DEFAULT NULL COMMENT 'refresh-token到期时间',
    `refresh_token_metadata`        text COMMENT 'refresh-token元数据',
    PRIMARY KEY (`id`)
) COMMENT 'oauth授权条目';

-- 已经登录的信息
DROP TABLE IF EXISTS `cloud_oauth2_authorization_consent`;
CREATE TABLE `cloud_oauth2_authorization_consent`
(
    `registered_client_id` varchar(100)  NOT NULL COMMENT '注册客户端ID',
    `user_id`              varchar(200)  NOT NULL COMMENT '用户ID',
    `authorities`          varchar(1000) NOT NULL COMMENT '选中的作用域',
    PRIMARY KEY (`registered_client_id`, `user_id`)
) COMMENT '首次授权同意记录';

-- 注册的客户端信息
DROP TABLE IF EXISTS `cloud_oauth2_registered_client`;
CREATE TABLE `cloud_oauth2_registered_client`
(
    `id`                              varchar(100)  NOT NULL COMMENT '注册客户端ID',
    `client_id`                       varchar(100)  NOT NULL COMMENT '客户端ID',
    `client_id_issued_at`             timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `client_secret`                   varchar(200)           DEFAULT NULL COMMENT '客户端密钥',
    `client_secret_expires_at`        timestamp NULL DEFAULT NULL COMMENT '客户端到期时间',
    `client_name`                     varchar(200)  NOT NULL COMMENT '客户端名称',
    `client_authentication_methods`   varchar(1000) NOT NULL COMMENT '认证方法逗号隔开',
    `authorization_grant_types`       varchar(1000) NOT NULL COMMENT '授权类型逗号隔开',
    `redirect_uris`                   varchar(1000)          DEFAULT NULL COMMENT '允许回调地址逗号隔开',
    `scopes`                          varchar(1000) NOT NULL COMMENT '作用域逗号隔开',
    `require_proof_key`               boolean       NOT NULL default false COMMENT '是否开启code_challenge校验',
    `require_authorization_consent`   boolean       NOT NULL default false COMMENT '是否开启首次同意的校验',
    `authorization_code_time_to_live` int           NOT NULL default 5 COMMENT 'code的有效时间(分)',
    `access_token_time_to_live`       int           NOT NULL default 5 COMMENT 'token的有效时间(分)',
    `access_token_format`             varchar(32)   NOT NULL default 'self-contained' COMMENT 'token的格式',
    `reuse_refresh_tokens`            boolean       NOT NULL default true COMMENT '是否重用refresh-token',
    `refresh_token_time_to_live`      int           NOT NULL default 60 COMMENT '刷新token的有效时间(分)',
    `id_token_signature_algorithm`    varchar(16)   NOT NULL default 'RS256' COMMENT 'id-token加密方式',
    PRIMARY KEY (`id`)
) COMMENT 'oauth注册客户端';
