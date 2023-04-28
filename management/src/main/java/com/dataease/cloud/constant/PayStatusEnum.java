package com.dataease.cloud.constant;

import com.wechat.pay.java.service.payments.model.Transaction;

/**
 * 统一支付状态
 */
public enum PayStatusEnum {
    /**
     * 转入退款
     */
    REFUND,
    /**
     * 交易创建，等待买家付款
     */
    WAIT,
    /**
     * 未付款交易超时关闭，或支付完成后全额退款
     */
    CLOSED,
    /**
     * 交易支付成功
     */
    SUCCESS,
    /**
     * 交易结束，不可退款
     */
    FINISHED;

    /**
     * 支付宝支付
     *
     * @param status
     * @return
     */
    public static PayStatusEnum ofAlipayStatus(String status) {
        switch (status) {
            case "WAIT_BUYER_PAY":
                return PayStatusEnum.WAIT;
            case "TRADE_CLOSED":
                return PayStatusEnum.CLOSED;
            case "TRADE_SUCCESS":
                return PayStatusEnum.SUCCESS;
            default:
                return PayStatusEnum.FINISHED;
        }
    }

    /**
     * 微信支付
     *
     * @param stateEnum
     * @return
     */
    public static PayStatusEnum ofWeixinPayStatus(Transaction.TradeStateEnum stateEnum) {
        for (PayStatusEnum value : values()) {
            if (value.name().equalsIgnoreCase(stateEnum.name())) {
                return value;
            }
        }
        return PayStatusEnum.WAIT;
    }
}
