package com.dataease.cloud.constant;

/**
 * 常量类
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/7 3:24 PM
 **/
public class Constants {

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";

    /**
     * 短信验证码有效时间 分钟
     */
    public static final int SMS_CODE_EXPIRE_TIME = 5;

    /**
     * 密码登录
     */
    public static final String PASSWORD_LOGIN_URI = "/user/login";

    /**
     * 短信登录的配置
     */
    public static final String PHONE_LOGIN_URI = "/phone/login";
    /**
     * 短信登录的手机字段名
     */
    public static final String PHONE_NUM_PARAMETER = "phone";
    /**
     * 短信登录的验证码名称
     */
    public static final String PHONE_CODE_PARAMETER = "code";

    /**
     * 短信发送
     */
    public static final String SMS_SEND_URI = "/sms/send";

    /**
     * 匿名访问URI
     */
    public static final String ANONYMOUS_URI = "/anonymous/**";

    /**
     * 用户注册的URI
     */
    public static final String REGISTER_URI = "/user/register";

    /**
     * 更新用户信息 这里一定要入数据库
     */
    public static final String USER_PROFILE = "/user/profile";

    /**
     * 外部源登录
     */
    public static final String EXTERNAL_LOGIN_URI = "/external/login";

    /**
     * 前端访问的路径
     */
    public static final String UI_URI = "/ui/**";

    /**
     * 保存用户信息
     */
    public static final String REQ_USER_ATTRIBUTE = "user_attr";

    /**
     * 滑动用的Key
     */
    public static final String SLIDER_CAPTCHA_REDIS_KEY = "slider_captcha_key::";

    /**
     * x轴允许的偏移值
     */
    public static final Integer SLIDER_CAPTCHA_ALLOW_DEVIATION = 5;

    /**
     * redis订阅主题
     */
    public static final String TOPIC_CUSTOMER = "cloud::ws::msg";

    /**
     * session清理的cron表达式
     * 仅对sessionType是redis和mysql有效
     * 默认每天晚上1:20触发
     * 这个后续移到运营中心即可 设置成Scheduled.CRON_DISABLED
     */
    public static final String SESSION_CLEANUP_CRON = "0 20 1 * * ?";

    /**
     * 默认一个星期触发一次
     * 每个星期6晚上2点
     * 这个后续移到运营中心即可设置成Scheduled.CRON_DISABLED
     */
    public static final String REMEMBER_ME_TOKEN_CLEANUP_CRON = "0 0 2 ? * SAT";
}
