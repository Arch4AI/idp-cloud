-- 用户jdbc session管理
DROP TABLE IF EXISTS `cloud_spring_session`;
CREATE TABLE cloud_spring_session
(
    primary_id            CHAR(36) NOT NULL,
    session_id            CHAR(36) NOT NULL,
    creation_time         BIGINT   NOT NULL,
    last_access_time      BIGINT   NOT NULL,
    max_inactive_interval INT      NOT NULL,
    expiry_time           BIGINT   NOT NULL,
    principal_name        VARCHAR(100),
    CONSTRAINT SPRING_SESSION_PK PRIMARY KEY (primary_id)
) ENGINE=InnoDB ROW_FORMAT=DYNAMIC;

CREATE UNIQUE INDEX SPRING_SESSION_IX1 ON cloud_spring_session (session_id);
CREATE INDEX SPRING_SESSION_IX2 ON cloud_spring_session (expiry_time);
CREATE INDEX SPRING_SESSION_IX3 ON cloud_spring_session (principal_name);
-- 用户jdbc session 的属性
DROP TABLE IF EXISTS `cloud_spring_session_attributes`;
CREATE TABLE cloud_spring_session_attributes
(
    session_primary_id CHAR(36)     NOT NULL,
    attribute_name     VARCHAR(200) NOT NULL,
    attribute_bytes    BLOB         NOT NULL,
    CONSTRAINT SPRING_SESSION_ATTRIBUTES_PK PRIMARY KEY (session_primary_id, attribute_name),
    CONSTRAINT SPRING_SESSION_ATTRIBUTES_FK FOREIGN KEY (session_primary_id) REFERENCES cloud_spring_session (PRIMARY_ID) ON DELETE CASCADE
) ENGINE=InnoDB ROW_FORMAT=DYNAMIC;

-- 用户单一redis session 关联管理
DROP TABLE IF EXISTS cloud_redis_session;
CREATE TABLE `cloud_redis_session`
(
    `session_id` varchar(64) NOT NULL COMMENT 'session_id',
    `user_id`    varchar(64) NOT NULL COMMENT '用户ID',
    PRIMARY KEY (`session_id`),
    INDEX        `index_user_id`(`user_id`) COMMENT '用户ID索引'
) COMMENT = '用户redis_session关联管理';
