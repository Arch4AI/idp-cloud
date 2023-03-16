-- 登录的用户
DROP TABLE IF EXISTS `cloud_oauth2_authorization`;
CREATE TABLE `cloud_oauth2_authorization`
(
    `id`                            varchar(100) NOT NULL,
    `registered_client_id`          varchar(100) NOT NULL,
    `principal_name`                varchar(200) NOT NULL,
    `authorization_grant_type`      varchar(100) NOT NULL,
    `authorized_scopes`             varchar(1000) DEFAULT NULL,
    `attributes`                    text,
    `state`                         varchar(500)  DEFAULT NULL,
    `authorization_code_value`      text,
    `authorization_code_issued_at`  timestamp NULL DEFAULT NULL,
    `authorization_code_expires_at` timestamp NULL DEFAULT NULL,
    `authorization_code_metadata`   text,
    `access_token_value`            text,
    `access_token_issued_at`        timestamp NULL DEFAULT NULL,
    `access_token_expires_at`       timestamp NULL DEFAULT NULL,
    `access_token_metadata`         text,
    `access_token_type`             varchar(100)  DEFAULT NULL,
    `access_token_scopes`           varchar(1000) DEFAULT NULL,
    `oidc_id_token_value`           text,
    `oidc_id_token_issued_at`       timestamp NULL DEFAULT NULL,
    `oidc_id_token_expires_at`      timestamp NULL DEFAULT NULL,
    `oidc_id_token_metadata`        text,
    `refresh_token_value`           text,
    `refresh_token_issued_at`       timestamp NULL DEFAULT NULL,
    `refresh_token_expires_at`      timestamp NULL DEFAULT NULL,
    `refresh_token_metadata`        text,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

-- 已经登录的信息
DROP TABLE IF EXISTS `cloud_oauth2_authorization_consent`;
CREATE TABLE `cloud_oauth2_authorization_consent`
(
    `registered_client_id` varchar(100)  NOT NULL,
    `principal_name`       varchar(200)  NOT NULL,
    `authorities`          varchar(1000) NOT NULL,
    PRIMARY KEY (`registered_client_id`, `principal_name`)
) ENGINE=InnoDB;

-- 注册的客户端信息
DROP TABLE IF EXISTS `cloud_oauth2_registered_client`;
CREATE TABLE `cloud_oauth2_registered_client`
(
    `id`                            varchar(100)  NOT NULL,
    `client_id`                     varchar(100)  NOT NULL,
    `client_id_issued_at`           timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `client_secret`                 varchar(200)  DEFAULT NULL,
    `client_secret_expires_at`      timestamp NULL DEFAULT NULL,
    `client_name`                   varchar(200)  NOT NULL,
    `client_authentication_methods` varchar(1000) NOT NULL,
    `authorization_grant_types`     varchar(1000) NOT NULL,
    `redirect_uris`                 varchar(1000) DEFAULT NULL,
    `scopes`                        varchar(1000) NOT NULL,
    `client_settings`               varchar(2000) NOT NULL,
    `token_settings`                varchar(2000) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;
