package com.dataease.cloud.service.file.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.dataease.cloud.entity.file.UploadResult;
import com.dataease.cloud.properties.FileProperties;
import com.dataease.cloud.properties.file.CosProperties;
import com.dataease.cloud.service.file.FileManagerService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.Download;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerConfiguration;
import com.qcloud.cos.transfer.Upload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * cos的实现
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/25 11:39 AM
 **/
@Service
@ConditionalOnProperty(prefix = "cloud.file",name = "provider",havingValue = "cos")
@EnableConfigurationProperties(FileProperties.class)
@Slf4j
public class CosFileManagerServiceImpl implements FileManagerService {

    private static final int nThreads = 32;
    private static final long multipartUploadThreshold = 5*1024*1024;
    private static final long minimumUploadPartSize = 1*1024*1024;

    private CosProperties cosProperties;

    public CosFileManagerServiceImpl(FileProperties fileProperties){
        this.cosProperties = fileProperties.getCos();
        log.info("init cos file manager");
    }

    @Override
    public UploadResult upload(MultipartFile file) throws Exception{
        TransferManager transferManager = createTransferManager();
        //获取文件key
        String fileKey = cosProperties.getFolderPath() + "/" + IdUtil.randomUUID() + "." + FileUtil.extName(file.getOriginalFilename());
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosProperties.getBucket(), fileKey, file.getInputStream(),null);
        Upload upload = transferManager.upload(putObjectRequest);
        // 等待上传完成
        upload.waitForUploadResult();
        // 关闭连接
        shutdownTransferManager(transferManager);
        return UploadResult.builder()
                .fileKey(fileKey)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .build();
    }

    @Override
    public void downFile(String fileKey, File file) throws Exception {
        TransferManager transferManager = createTransferManager();
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosProperties.getBucket(),fileKey);
        Download download = transferManager.download(getObjectRequest,file);
        download.waitForCompletion();
        //关闭套接字
        shutdownTransferManager(transferManager);
    }

    @Override
    public void deleteFile(String fileKey) {
        COSClient cosClient = createClient();
        cosClient.deleteObject(cosProperties.getBucket(),fileKey);
        //移除链接
        cosClient.shutdown();
    }

    /**
     * 关闭这个
     * @param transferManager
     */
    private void shutdownTransferManager(TransferManager transferManager) {
        transferManager.shutdownNow(true);
    }

    /**
     * 分片上传的东西
     * @return
     */
    private TransferManager createTransferManager(){
        COSClient cosClient = createClient();
        ExecutorService threadPool = Executors.newFixedThreadPool(nThreads);
        TransferManager transferManager = new TransferManager(cosClient, threadPool);
        TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
        transferManagerConfiguration.setMultipartUploadThreshold(multipartUploadThreshold);
        transferManagerConfiguration.setMinimumUploadPartSize(minimumUploadPartSize);
        transferManager.setConfiguration(transferManagerConfiguration);
        return transferManager;
    }

    /**
     * 获取Cos连接
     * @return
     */
    private COSClient createClient(){
        COSCredentials cred = new BasicCOSCredentials(cosProperties.getAccessKey(), cosProperties.getSecretKey());
        Region region = new Region(cosProperties.getRegionId());
        ClientConfig clientConfig = new ClientConfig(region);
        clientConfig.setHttpProtocol(HttpProtocol.https);
        return new COSClient(cred, clientConfig);
    }
}
