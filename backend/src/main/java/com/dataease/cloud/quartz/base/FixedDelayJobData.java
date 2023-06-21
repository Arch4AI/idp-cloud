package com.dataease.cloud.quartz.base;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 延时任务
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/17 11:20 AM
 **/
public final class FixedDelayJobData implements Serializable {
    private static final long serialVersionUID = 1L;
    private long delay;
    private TimeUnit delayUnit;

    public FixedDelayJobData(long delay) {
        this(delay, TimeUnit.MILLISECONDS);
    }

    public FixedDelayJobData(long delay, TimeUnit delayUnit) {
        if (delay == 0L) {
            throw new IllegalArgumentException("Delay cannot be zero");
        } else if (delayUnit == null) {
            throw new IllegalArgumentException("Delay Unit should be provided");
        } else {
            this.delay = delay;
            this.delayUnit = delayUnit;
        }
    }

    public Date getNextScheduleDate() {
        return new Date(System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(this.delay, this.delayUnit));
    }

    public String toString() {
        return "FixedDelayJobData [delay=" + this.delay + ", delayUnit=" + this.delayUnit + "]";
    }
}
