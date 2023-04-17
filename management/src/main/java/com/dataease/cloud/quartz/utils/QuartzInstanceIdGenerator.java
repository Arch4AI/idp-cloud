package com.dataease.cloud.quartz.utils;

import org.quartz.spi.InstanceIdGenerator;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * quartz的ID生成器
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/17 10:57 AM
 **/
public class QuartzInstanceIdGenerator implements InstanceIdGenerator {

    public QuartzInstanceIdGenerator() {
    }

    public String generateInstanceId() {
        try {
            return InetAddress.getLocalHost().getHostName() + System.currentTimeMillis();
        } catch (UnknownHostException var2) {
            return "unknown-" + System.currentTimeMillis();
        }
    }
}
