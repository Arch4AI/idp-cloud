package com.dataease.cloud.utils;

import lombok.Data;

/**
 * 返回滑动验证的构建信息
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/10 2:57 PM
 **/
@Data
public class CaptchaDto {
    //随机字符串
    private String ticket;
    //验证值
    private String value;
    //生成的画布的base64
    private String canvasSrc;
    //画布宽度
    private Integer canvasWidth;
    //画布高度
    private Integer canvasHeight;
    //生成的阻塞块的base64
    private String blockSrc;
    //阻塞块宽度
    private Integer blockWidth;
    //阻塞块高度
    private Integer blockHeight;
    //阻塞块凸凹半径
    private Integer blockRadius;
    // 阻塞块的横轴坐标
    private Integer blockX;
    // 阻塞块的纵轴坐标
    private Integer blockY;
    //图片获取来源
    private Integer place = 1;
}
