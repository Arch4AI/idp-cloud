package com.dataease.cloud.properties;

import com.dataease.cloud.constant.SessionType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.session.data.redis.RedisSessionRepository;
import org.springframework.stereotype.Component;

/**
 * session配置
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/18 2:58 PM
 **/
@Component
@ConfigurationProperties(prefix = "cloud.session")
@Data
public class SessionProperties {

    /**
     * session到期时间<这里指的只是登录后的Session>
     */
    private int sessionTimeOut = 86400;

    /**
     * 单用户最大在线数量
     */
    private int maximumSessions = -1;

    /**
     * 记住我cookie加密Key
     */
    private String rememberTokenKey = "cloud_idp";

    /**
     * 是否开启记住我 默认true
     */
    private boolean autoRememberMe = true;

    /**
     * 记住我的token时间 秒 默认两个星期
     */
    private int rememberMeTokenTime = AbstractRememberMeServices.TWO_WEEKS_S;

    /**
     * 记住我的cookieName
     */
    private String rememberCookieName = "CLOUD_REMEMBER_ME";

    /**
     * session持久化类型 默认是JDBC
     */
    private SessionType sessionType = SessionType.JDBC;

    /**
     * 防止超过最大session登录 默认不禁止
     */
    private boolean maxSessionsPreventsLogin = false;

    /**
     * redis session的命名空间
     */
    private static String redisNamespace = RedisSessionRepository.DEFAULT_KEY_NAMESPACE;



    public static String getRedisNamespace() {
        return redisNamespace;
    }

    public void setRedisNamespace(String redisNamespace) {
        SessionProperties.redisNamespace = redisNamespace;
    }
}
