package com.dataease.cloud.service.file.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import com.dataease.cloud.entity.file.UploadResult;
import com.dataease.cloud.exception.CloudException;
import com.dataease.cloud.service.file.FileManagerService;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 本地默认的上传下载
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/25 5:54 PM
 **/
public class DefaultFileManagerServiceImpl implements FileManagerService {

    //默认放到tmp下面
    private static String DEFAULT_FILE_BASE_PATH="/tmp";
    private static String DEFAULT_FILE_FLODER_PATH = "file";

    @Override
    public UploadResult upload(MultipartFile file) throws Exception {
        String fileKey = DEFAULT_FILE_FLODER_PATH+ "/" + IdUtil.randomUUID() + "." + FileUtil.extName(file.getOriginalFilename());
        File localFile = new File(DEFAULT_FILE_BASE_PATH + "/" + fileKey);
        IoUtil.copy(file.getInputStream(),FileUtil.getOutputStream(localFile));
        return UploadResult.builder()
                .fileKey(fileKey)
                .fileSize(file.getSize())
                .fileName(file.getOriginalFilename())
                .build();
    }

    @Override
    public void downFile(String fileKey, File file) throws Exception {
        File localFile = new File(DEFAULT_FILE_BASE_PATH + "/" + fileKey);
        if(!localFile.exists())
            CloudException.throwException("Not Found File");
        IoUtil.copy(FileUtil.getInputStream(localFile),FileUtil.getOutputStream(file));
    }

    @Override
    public void deleteFile(String fileKey) {
        File localFile = new File(DEFAULT_FILE_BASE_PATH + "/" + fileKey);
        FileUtil.del(localFile);
    }
}
