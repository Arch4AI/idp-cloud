package com.dataease.cloud.properties.file;

import lombok.Data;

/**
 * cos的基本配置
 */
@Data
public class CosProperties {
    //ak
    private String ak;
    //sk
    private String sk;
    //存储文件夹
    private String folderPath;
    //region
    private String region;
    //桶
    private String bucket;
}
