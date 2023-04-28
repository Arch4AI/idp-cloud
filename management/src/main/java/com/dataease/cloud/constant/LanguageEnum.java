package com.dataease.cloud.constant;

import java.util.Locale;

/**
 * 语言枚举
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/7 2:48 PM
 **/
public enum LanguageEnum {

    ZH_CN("zh_CN", Locale.SIMPLIFIED_CHINESE),
    ZH_TW("zh_TW", Locale.TRADITIONAL_CHINESE),
    EN_US("en_US", Locale.US);

    private String name;
    private Locale locale;

    LanguageEnum(String name, Locale locale) {
        this.name = name;
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public Locale getLocale() {
        return locale;
    }

    /**
     * 根据名称返回Locale
     *
     * @param name
     * @return
     */
    public static Locale getLocalByName(String name) {
        for (LanguageEnum value : values()) {
            if (value.name.equalsIgnoreCase(name)) {
                return value.locale;
            }
        }
        return Locale.SIMPLIFIED_CHINESE;
    }
}
