package com.dataease.cloud.utils;

import cn.hutool.core.util.StrUtil;
import org.springframework.core.env.Environment;

/**
 * 标准使用
 */
public class GlobalConfigurations {

    private static Environment environment;

    public GlobalConfigurations(Environment environment) {
        GlobalConfigurations.environment = environment;
    }

    public static boolean isReleaseMode() {
        return StrUtil.equals(environment.getProperty("run.mode", "local"), "release");
    }
}
