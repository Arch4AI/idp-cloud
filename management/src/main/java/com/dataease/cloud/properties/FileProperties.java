package com.dataease.cloud.properties;

import com.dataease.cloud.constant.FileProvider;
import com.dataease.cloud.properties.file.CosProperties;
import com.dataease.cloud.properties.file.OssProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 文件相关属性
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/26 9:57 AM
 **/
@Data
@ConfigurationProperties(value = "cloud.file", ignoreUnknownFields = true)
public class FileProperties {
    private FileProvider provider = FileProvider.local;
    private CosProperties cos;
    private OssProperties oss;
}
