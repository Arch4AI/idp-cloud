package com.dataease.cloud.properties.file;

import lombok.Data;

/**
 * oss对象存储属性
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/25 5:35 PM
 **/
@Data
public class OssProperties {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String folderPath;
}
