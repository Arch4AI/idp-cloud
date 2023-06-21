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
    login_location       varchar(255) DEFAULT '' COMMENT '登录地点',
    browser              varchar(50)  DEFAULT '' COMMENT '浏览器类型',
    os                   varchar(50)  DEFAULT '' COMMENT '操作系统',
    status               int(1) DEFAULT 0 COMMENT '登录状态（0失败 1成功）',
    msg                  varchar(255) DEFAULT '' COMMENT '提示消息',
    login_time           DATETIME COMMENT '登录时间',
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





