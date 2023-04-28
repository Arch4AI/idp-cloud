package com.dataease.cloud.service;

import com.dataease.cloud.base.domain.CloudSysOperLog;
import com.dataease.cloud.base.mapper.CloudSysOperLogMapper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "baseTransactionManager")
public class SystemLogService {

    @Resource
    @Lazy
    private CloudSysOperLogMapper cloudSysOperLogMapper;

    @Async
    public void addSystemLog(CloudSysOperLog cloudSysOperLog) {
        cloudSysOperLogMapper.insertSelective(cloudSysOperLog);
    }
}
