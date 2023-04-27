package com.dataease.cloud.pay;

import com.dataease.cloud.entity.request.pay.PayOrderRequest;
import com.dataease.cloud.entity.response.pay.PayOrderResponse;
import com.dataease.cloud.entity.response.pay.PayResultResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 抽象的支付类
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/27 10:14 AM
 **/
public abstract class AbstractPayService {

    /**
     * 创建支付订单
     *
     * @param payOrderRequest
     * @return
     */
    public abstract PayOrderResponse createPayOrder(PayOrderRequest payOrderRequest);

    /**
     * 根据商户号查询订单详情
     *
     * @param orderId
     * @return
     */
    public abstract PayResultResponse queryPayResult(String orderId);

    /**
     * 支付后的异步通知解析
     *
     * @param body
     * @param request
     * @return
     */
    public abstract PayResultResponse payNotify(String body, HttpServletRequest request);
}
