package com.dataease.cloud.manager.factory;

import com.dataease.cloud.base.domain.SysOperLog;
import com.dataease.cloud.base.mapper.SysOperLogMapper;
import com.dataease.cloud.utils.SpringUtils;
import java.util.TimerTask;

/**
 * 异步工厂（产生任务用）
 *
 * @author junhong
 */
public class AsyncFactory {
    /**
     * 操作日志记录
     *
     * @param operLog 操作日志信息
     * @return 任务task
     */
    public static TimerTask recordOper(final SysOperLog operLog) {
        return new TimerTask() {
            @Override
            public void run() {
                SpringUtils.getBean(SysOperLogMapper.class).insertSelective(operLog);
            }
        };
    }
}
