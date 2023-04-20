package com.dataease.cloud.config;

import com.dataease.cloud.constant.Constants;
import com.dataease.cloud.security.session.jdbc.JdbcSessionRegistryImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

import javax.sql.DataSource;


/**
 * 开启jdbc http session
 * 这个是默认的
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/6 11:25 AM
 **/
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(DataSource.class)
@ConditionalOnProperty(prefix = "cloud.session", value = "session-type", havingValue = "jdbc", matchIfMissing = true)
@EnableJdbcHttpSession(tableName = "cloud_spring_session", cleanupCron = Constants.SESSION_CLEANUP_CRON)
public class JdbcSessionConfig {

    @Bean
    public SessionRegistry sessionRegistry(JdbcIndexedSessionRepository sessionRepository) {
        JdbcSessionRegistryImpl sessionRegistry = new JdbcSessionRegistryImpl(sessionRepository);
        return sessionRegistry;
    }
}
