DROP TABLE IF EXISTS `cloud_remember_me_token`;
CREATE TABLE `cloud_remember_me_token`
(
    `series`    varchar(64) NOT NULL COMMENT '系列',
    `user_id`  varchar(64) NOT NULL COMMENT '用户ID',
    `token`     varchar(64) NOT NULL COMMENT 'token',
    `session_id` varchar(64) NOT NULL COMMENT '会话ID',
    `last_used` timestamp   NOT NULL COMMENT '最后更新时间',
    PRIMARY KEY (`series`)
) COMMENT = 'RememberMe-Token持久化';
