package com.dataease.cloud.utils;

import com.dataease.cloud.properties.CommonProperties;
import com.fit2cloud.cloud.http.HttpUtil;
import com.fit2cloud.cloud.http.support.SimpleHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 地址工具类
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/6 11:02 AM
 **/
public class AddressUtils {
    private static final Logger log = LoggerFactory.getLogger(AddressUtils.class);

    // IP地址查询
    public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp";

    // 未知地址
    public static final String UNKNOWN = "未知";

    public static String getRealAddressByIP(String ip) {
        // 内网不查询
        if (IpUtils.internalIp(ip)) {
            return "局域网";
        }
        if (CommonProperties.isAddressEnabled()) {
            try {
                SimpleHttpResponse simpleHttpResponse = HttpUtil.get(String.format("%s?ip=%s&json=true", IP_URL, ip));
                String rspStr = simpleHttpResponse.getBody();
                if (!StringUtils.hasText(rspStr)) {
                    log.error("获取地理位置异常 {}", ip);
                    return UNKNOWN;
                }
                Map<String,String> obj = ObjectMapperUtils.simpleObjectMapper.readValue(rspStr, Map.class);
                String region = obj.get("pro");
                String city = obj.get("city");
                return String.format("%s %s", region, city);
            } catch (Exception e) {
                log.error("获取地理位置异常 {}", ip);
            }
        }
        return UNKNOWN;
    }
}
