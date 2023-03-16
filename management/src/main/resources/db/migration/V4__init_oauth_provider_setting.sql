DROP TABLE IF EXISTS cloud_external_auth_source;
CREATE TABLE `cloud_external_auth_source`
(
    `id`                 varchar(64)  NOT NULL COMMENT '主键',
    `display_name`       varchar(255) NOT NULL COMMENT '提供者显示名称',
    `provider_icon`      varchar(255) NULL COMMENT '图标',
    `client_id`          varchar(255) NOT NULL COMMENT '客户端ID',
    `client_secret`      varchar(255) NOT NULL COMMENT '客户端密钥',
    `client_type`        varchar(255) NOT NULL COMMENT '客户端类型',
    `redirect_uri`       varchar(255) NOT NULL COMMENT '重定向URI',
    `auth_uri`           varchar(255) NULL COMMENT '认证URI',
    `token_uri`          varchar(255) NULL COMMENT '令牌URI',
    `user_info_uri`      varchar(255) NULL COMMENT '用户信息URI',
    `logout_uri`         varchar(255) NULL COMMENT '登出URI',
    `alipay_public_key`  varchar(255) NULL COMMENT '支付宝公钥',
    `stack_overflow_key` varchar(255) NULL COMMENT 'stackoverflow key',
    `agent_id`           varchar(255) NULL COMMENT '企业微信ID',
    `user_type`          varchar(255) NULL COMMENT '企业微信授权对象',
    `domain_prefix`      varchar(255) NULL COMMENT '域名前缀(coding/okta)',
    `device_id`          varchar(255) NULL COMMENT '设备ID',
    `auth_server_id`     varchar(255) NULL COMMENT 'Okta 授权服务器的 ID',
    `scope`              varchar(255) NULL COMMENT '作用域',
    `open_proxy`         boolean NULL COMMENT '是否启用代理',
    `proxy_host`         varchar(255) NULL COMMENT '代理地址',
    `proxy_port`         int NULL COMMENT '代理端口',
    `enable`             boolean      NOT NULL DEFAULT true COMMENT '是否可用',
    `create_time`        datetime(0) NULL COMMENT '创建时间',
    `update_time`        datetime(0) NULL COMMENT '修改时间',
    PRIMARY KEY (`id`)
) COMMENT = '外部认证源';

DROP TABLE IF EXISTS cloud_user_auth_source_relation;
CREATE TABLE `cloud_user_auth_source_relation`
(
    `auth_source_id` varchar(64)  NOT NULL COMMENT '认证源ID',
    `auth_user_uuid` varchar(255) NOT NULL COMMENT '用户UUID ',
    `user_id`        varchar(64) NULL COMMENT '系统用户ID',
    `auth_user_name` varchar(255) NULL COMMENT '用户名',
    `auth_nick_name` varchar(255) NULL COMMENT '用户昵称',
    `auth_avatar`    varchar(255) NULL COMMENT '用户头像',
    `auth_email`     varchar(255) NULL COMMENT '用户邮箱',
    `auth_source`    varchar(255) NULL COMMENT '用户来源',
    `auth_user_raw`  longtext NULL COMMENT '用户原始数据',
    `create_time`    datetime(0) NULL COMMENT '创建时间',
    `update_time`    datetime(0) NULL COMMENT '修改时间',
    PRIMARY KEY (`auth_source_id`, `auth_user_uuid`),
    INDEX            `idx_user_id`(`user_id`) USING BTREE COMMENT '用户ID索引'
) COMMENT = '用户与认证源的关系表';



