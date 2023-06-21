package com.dataease.cloud.service;

import com.dataease.cloud.base.domain.CloudSysOperLogWithBLOBs;
import com.dataease.cloud.base.mapper.CloudSysOperLogMapper;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class SystemLogService {

    @Resource
    private CloudSysOperLogMapper cloudSysOperLogMapper;

    @Async
    public void addSystemLog(CloudSysOperLogWithBLOBs cloudSysOperLog) {
        cloudSysOperLogMapper.insertSelective(cloudSysOperLog);
    }
}
