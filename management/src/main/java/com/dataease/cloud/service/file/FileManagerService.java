package com.dataease.cloud.service.file;

import com.dataease.cloud.entity.request.file.UploadResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 文件管理服务
 * 默认会支持Local/oss/cos
 * 默认只会实现其中一种 不会兼并
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/25 11:21 AM
 **/
public interface FileManagerService {

    /**
     * 但文件上传
     *
     * @param file 来自前端上传的文件
     * @return
     */
    public UploadResult upload(MultipartFile file) throws Exception;

    /**
     * 文件下载
     *
     * @param fileKey 文件地址
     * @param file    需要写入的新文件
     */
    public void downFile(String fileKey, File file) throws Exception;

    /**
     * 删除文件
     *
     * @param fileKey
     */
    public void deleteFile(String fileKey);
}
