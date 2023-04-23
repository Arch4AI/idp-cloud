-- 給cloud_oauth2_authorization添加一個sessionId紀錄用戶是從這個session進行登陸的
alter table cloud_oauth2_authorization add column session_id CHAR(36) default null COMMENT 'session_id';
