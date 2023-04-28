package com.dataease.cloud.payment.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建支付订单返回的信息
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/26 6:09 PM
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayOrderResponse {
    private String orderId;//订单号
    private String qrCode;//二维码地址 需要在前端转成二维码地址
    private String htmlContent;//嵌入的内容 需要iframe
}
