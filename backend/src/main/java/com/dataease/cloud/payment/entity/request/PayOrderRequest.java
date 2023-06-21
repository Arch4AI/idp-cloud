package com.dataease.cloud.payment.entity.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建支付订单的请求
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/26 6:07 PM
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayOrderRequest {
    private String orderId; // 平台订单号 不能大于32位切记
    private int amount;// 单位是分
    private String description;//商品描述
    private String returnUrl;//支付完成后返回的页面
}
