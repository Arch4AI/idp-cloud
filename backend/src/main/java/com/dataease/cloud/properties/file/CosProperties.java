package com.dataease.cloud.properties.file;

import lombok.Data;

/**
 * cos的基本配置
 */
@Data
public class CosProperties {
    //ak
    private String accessKey;
    //sk
    private String secretKey;
    //存储文件夹
    private String folderPath;
    //region
    private String regionId;
    //桶
    private String bucket;
}
