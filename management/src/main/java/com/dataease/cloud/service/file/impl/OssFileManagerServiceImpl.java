package com.dataease.cloud.service.file.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.dataease.cloud.entity.file.UploadResult;
import com.dataease.cloud.properties.file.OssProperties;
import com.dataease.cloud.service.file.FileManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * oss文件管理
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/25 1:46 PM
 **/
@Slf4j
public class OssFileManagerServiceImpl implements FileManagerService {

    private OssProperties ossProperties;

    @Override
    public UploadResult upload(MultipartFile file) throws Exception {
        OSS ossClient = new OSSClientBuilder().build(ossProperties.getEndpoint(),ossProperties.getAccessKeyId(),ossProperties.getAccessKeySecret());
        String fileKey = ossProperties.getFolderPath() + "/" + IdUtil.randomUUID() + "." + FileUtil.extName(file.getOriginalFilename());
        try {
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(ossProperties.getBucketName(), fileKey);
            InitiateMultipartUploadResult upresult = ossClient.initiateMultipartUpload(request);
            String uploadId = upresult.getUploadId();
            List<PartETag> partETags =  new ArrayList<>();
            final long partSize = 1 * 1024 * 1024L;   //1 MB。
            long fileLength = fileKey.length();
            int partCount = (int) (fileLength / partSize);
            if (fileLength % partSize != 0) {
                partCount++;
            }
            for (int i = 0; i < partCount; i++) {
                long startPos = i * partSize;
                long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(ossProperties.getBucketName());
                uploadPartRequest.setKey(fileKey);
                uploadPartRequest.setUploadId(uploadId);
                // 设置上传的分片流。
                // 以本地文件为例说明如何创建FIleInputstream，并通过InputStream.skip()方法跳过指定数据。
                InputStream instream = file.getInputStream();
                instream.skip(startPos);
                uploadPartRequest.setInputStream(instream);
                // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB。
                uploadPartRequest.setPartSize(curPartSize);
                // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出此范围，OSS将返回InvalidArgument错误码。
                uploadPartRequest.setPartNumber( i + 1);
                // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
                UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
                // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在partETags中。
                partETags.add(uploadPartResult.getPartETag());
            }
            CompleteMultipartUploadRequest completeMultipartUploadRequest =
                    new CompleteMultipartUploadRequest(ossProperties.getBucketName(), fileKey, uploadId, partETags);
            CompleteMultipartUploadResult completeMultipartUploadResult = ossClient.completeMultipartUpload(completeMultipartUploadRequest);
            return UploadResult.builder()
                    .fileName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .fileKey(fileKey)
                    .build();
        }catch (Exception e){
            log.error("oss upload file error:",e);
            throw e;
        }finally {
            if(ossClient != null)
                ossClient.shutdown();
        }
    }

    @Override
    public void downFile(String fileKey, File file) throws Exception {
        OSS ossClient = new OSSClientBuilder().build(ossProperties.getEndpoint(),ossProperties.getAccessKeyId(),ossProperties.getAccessKeySecret());
        try {
            ossClient.getObject(new GetObjectRequest(ossProperties.getBucketName(), fileKey),file);
        }catch (Exception e){
            log.error("oss down file error:",e);
            throw e;
        }finally {
            if(ossClient != null)
                ossClient.shutdown();
        }
    }

    @Override
    public void deleteFile(String fileKey) {
        OSS ossClient = new OSSClientBuilder().build(ossProperties.getEndpoint(),ossProperties.getAccessKeyId(),ossProperties.getAccessKeySecret());
        ossClient.deleteObject(ossProperties.getBucketName(),fileKey);
        if(ossClient != null)
            ossClient.shutdown();
    }
}
