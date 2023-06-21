package com.dataease.cloud.constant;

/**
 * 正则表达式常量
 * 来源 https://any-rule.vercel.app/
 * https://regex101.com/
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/8 6:06 PM
 **/
public final class RegxContants {
    // 手机号(mobile phone)中国(最宽松), 只要是1开头即可
    public static final String PHONE_NUM_REGX = "^(?:(?:\\+|00)86)?1\\d{10}$";
    // 邮箱正则 支持中文邮箱
    public static final String EMAIL_REGX = "^[A-Za-z0-9\\u4e00-\\u9fa5_]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    // 用户名校验，4到16位（字母，数字，下划线，减号）
    public static final String USER_NAME_REGX = "^[\\w-]{4,16}$";
    // 密码强度校验，最少6位，1个小写字母包括至少1个大写字母，，1个数字，1个特殊字符
    public static final String PASSWORD_REGX = "^\\S*(?=\\S{6,})(?=\\S*\\d)(?=\\S*[A-Z])(?=\\S*[a-z])(?=\\S*[!@#$%^&*.?])\\S*$";
    // 一二代身份证校验
    public static final String ID_CARD_REGX = "^\\d{6}((((((19|20)\\d{2})(0[13-9]|1[012])(0[1-9]|[12]\\d|30))|(((19|20)\\d{2})(0[13578]|1[02])31)|((19|20)\\d{2})02(0[1-9]|1\\d|2[0-8])|((((19|20)([13579][26]|[2468][048]|0[48]))|(2000))0229))\\d{3})|((((\\d{2})(0[13-9]|1[012])(0[1-9]|[12]\\d|30))|((\\d{2})(0[13578]|1[02])31)|((\\d{2})02(0[1-9]|1\\d|2[0-8]))|(([13579][26]|[2468][048]|0[048])0229))\\d{2}))(\\d|X|x)$";

    //包含ip:port 域名:port的形式
    public static final String URL_REGX = "^((https?:)(\\/\\/\\/?)([\\w]*(?::[\\w]*)?@)?([\\d\\w\\.-]+)(?::(\\d+))?)?([\\/\\\\\\w\\.()-]*)?(?:([?][^#]*)?(#.*)?)*";
}
