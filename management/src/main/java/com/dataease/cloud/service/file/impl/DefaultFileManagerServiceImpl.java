package com.dataease.cloud.service.file.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import com.dataease.cloud.entity.request.file.UploadResult;
import com.dataease.cloud.exception.CloudException;
import com.dataease.cloud.i18n.I18nService;
import com.dataease.cloud.service.file.FileManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 本地默认的上传下载
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/25 5:54 PM
 **/
@Service
@ConditionalOnProperty(prefix = "cloud.file", name = "provider", havingValue = "local", matchIfMissing = true)
@Slf4j
public class DefaultFileManagerServiceImpl implements FileManagerService {

    //默认放到tmp下面
    private static String DEFAULT_FILE_BASE_PATH = "/tmp";
    private static String DEFAULT_FILE_FLODER_PATH = "file";

    public DefaultFileManagerServiceImpl() {
        log.info("init defalut file manager");
    }

    @Override
    public UploadResult upload(MultipartFile file) throws Exception {
        String fileKey = DEFAULT_FILE_FLODER_PATH + "/" + IdUtil.randomUUID() + "." + FileUtil.extName(file.getOriginalFilename());
        File localFile = new File(DEFAULT_FILE_BASE_PATH + "/" + fileKey);
        IoUtil.copy(file.getInputStream(), FileUtil.getOutputStream(localFile));
        return UploadResult.builder()
                .fileKey(fileKey)
                .fileSize(file.getSize())
                .fileName(file.getOriginalFilename())
                .build();
    }

    @Override
    public void downFile(String fileKey, File file) throws Exception {
        File localFile = new File(DEFAULT_FILE_BASE_PATH + "/" + fileKey);
        if (!localFile.exists())
            CloudException.throwException(I18nService.getMessage("file_not_found"));
        IoUtil.copy(FileUtil.getInputStream(localFile), FileUtil.getOutputStream(file));
    }

    @Override
    public void deleteFile(String fileKey) {
        File localFile = new File(DEFAULT_FILE_BASE_PATH + "/" + fileKey);
        FileUtil.del(localFile);
    }
}
