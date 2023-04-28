package com.dataease.cloud.entity.request.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 上传文件需要返回的信息
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/25 11:25 AM
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResult {
    /**
     * 唯一key 最终存放的地址
     * 如果是对象存储的话 应该是floder/new file name
     */
    private String fileKey;
    /**
     * 文件的原始名称 用于下载使用
     */
    private String fileName;
    /**
     * 文件的大小
     */
    private long fileSize;

}
