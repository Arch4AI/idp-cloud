package com.dataease.cloud.payment.service;

import com.dataease.cloud.payment.entity.request.PayOrderRequest;
import com.dataease.cloud.payment.entity.response.PayOrderResponse;
import com.dataease.cloud.payment.entity.response.PayResultResponse;
import com.dataease.cloud.payment.properties.BasePayProperties;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 抽象的支付类
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/27 10:14 AM
 **/
public abstract class AbstractPayService<T extends BasePayProperties> {

    protected T properties;

    public AbstractPayService(T properties) {
        //进来的时候校验一下参数
        properties.validate();
        this.properties = properties;
    }

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
