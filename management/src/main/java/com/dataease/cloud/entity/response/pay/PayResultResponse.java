package com.dataease.cloud.entity.response.pay;

import com.dataease.cloud.constant.PayStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付结果集 合并的
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/27 9:07 AM
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayResultResponse {
    private String appId;//应用ID
    private String outTradeNo;//系统订单号
    private String transactionId;//交易流水号
    private String buyerId;//买家ID
    private PayStatusEnum status;//交易的状态
    private int amount;//订单总金额 单位是分
    private int payAmount;//支付金额 单位分
    private String currency;//交易币种 默认是CNY
    private String subject;//订单的标题 微信支付无法获取到这个
}
