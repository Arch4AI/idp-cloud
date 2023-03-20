DROP TABLE IF EXISTS cloud_user;
CREATE TABLE cloud_user
(
    user_id     VARCHAR(64) NOT NULL COMMENT '用户ID',
    user_name   VARCHAR(255) COMMENT '用户名',
    nick_name   VARCHAR(255) COMMENT '显示名称',
    gender      INT(2) COMMENT '性别',
    phone       VARCHAR(255) COMMENT '手机号码',
    email       VARCHAR(255) COMMENT '邮箱',
    password    VARCHAR(255) COMMENT '密码',
    enabled     BOOLEAN COMMENT '是否可用',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '修改时间',
    language    VARCHAR(32) COMMENT '语言',
    PRIMARY KEY (user_id)
) COMMENT = '用户表';


CREATE UNIQUE INDEX index_user_name ON cloud_user (user_name);
CREATE UNIQUE INDEX index_phone ON cloud_user (phone);
CREATE UNIQUE INDEX index_email ON cloud_user (email);

DROP TABLE IF EXISTS cloud_user_attribute;
CREATE TABLE cloud_user_attribute
(
    user_id         VARCHAR(64) NOT NULL COMMENT '用户ID',
    attribute_key   VARCHAR(64) NOT NULL COMMENT '属性key',
    attribute_name  VARCHAR(255) COMMENT '属性名称',
    attribute_value VARCHAR(255) COMMENT '属性值',
    create_time     DATETIME COMMENT '创建时间',
    update_time     DATETIME COMMENT '更新时间',
    PRIMARY KEY (user_id, attribute_key)
) COMMENT = '用户属性表';

DROP TABLE IF EXISTS cloud_user_login_log;
CREATE TABLE cloud_user_login_log
(
    id                   BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id              VARCHAR(64) COMMENT '用户ID',
    registered_client_id BIGINT COMMENT '注册客户端ID;外部登录的',
    session_id           VARCHAR(255) COMMENT 'sessionId;sessionId唯一',
    ip                   VARCHAR(255) COMMENT 'IP地址',
    login_time           DATETIME COMMENT '登录时间',
    logout_time          DATETIME COMMENT '登出时间',
    PRIMARY KEY (id)
) COMMENT = '用户登录日志';

DROP TABLE IF EXISTS cloud_user_role;
CREATE TABLE cloud_user_role
(
    user_id     VARCHAR(64) NOT NULL COMMENT '用户ID',
    role        VARCHAR(64) NOT NULL COMMENT '角色',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '修改时间',
    PRIMARY KEY (user_id, role)
) COMMENT = '用户角色表';

-- 用户session管理
DROP TABLE IF EXISTS cloud_session;
CREATE TABLE `cloud_session`
(
    `session_id`      varchar(64) NOT NULL COMMENT 'session_id',
    `user_id`         varchar(64) NOT NULL COMMENT '用户ID',
    `create_time`     datetime(0) NULL COMMENT '创建时间',
    `last_quest_time` datetime(0) NULL COMMENT '更新时间',
    `expire_time`     datetime(0) NULL COMMENT '到期时间',
    `expired`         boolean NULL COMMENT '是否已经到期',
    PRIMARY KEY (`session_id`),
    INDEX             `index_user_id`(`user_id`, `expired`) COMMENT '用户ID索引'
) COMMENT = '用户session管理';





