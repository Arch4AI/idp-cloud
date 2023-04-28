package com.dataease.cloud.pay.weixin;

import com.dataease.cloud.constant.PayStatusEnum;
import com.dataease.cloud.entity.request.pay.PayOrderRequest;
import com.dataease.cloud.entity.response.pay.PayOrderResponse;
import com.dataease.cloud.entity.response.pay.PayResultResponse;
import com.dataease.cloud.pay.AbstractPayService;
import com.dataease.cloud.properties.pay.WeixinPayProperties;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import com.wechat.pay.java.service.payments.nativepay.model.QueryOrderByOutTradeNoRequest;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 微信支付
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/26 6:00 PM
 **/
public class WeixinPayService extends AbstractPayService {

    private WeixinPayProperties weixinPayProperties;

    public WeixinPayService(WeixinPayProperties weixinPayProperties) {
        this.weixinPayProperties = weixinPayProperties;
    }

    @Override
    public PayOrderResponse createPayOrder(PayOrderRequest payOrderRequest) {
        Config config =
                new RSAAutoCertificateConfig.Builder()
                        .merchantId(weixinPayProperties.getMerchantId())
                        .privateKey(weixinPayProperties.getPrivateKey())
                        .merchantSerialNumber(weixinPayProperties.getMerchantSerialNumber())
                        .apiV3Key(weixinPayProperties.getApiV3Key())
                        .build();
        NativePayService service = new NativePayService.Builder().config(config).build();
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(payOrderRequest.getAmount());
        request.setAmount(amount);
        request.setAppid(weixinPayProperties.getAppId());
        request.setMchid(weixinPayProperties.getMerchantId());
        request.setDescription(payOrderRequest.getDescription());
        request.setNotifyUrl(weixinPayProperties.getNotifyUrl());
        request.setOutTradeNo(payOrderRequest.getOrderId());
        // 调用下单方法，得到应答
        PrepayResponse response = service.prepay(request);
        return PayOrderResponse.builder()
                .orderId(payOrderRequest.getOrderId())
                .qrCode(response.getCodeUrl())
                .build();
    }

    @Override
    public PayResultResponse queryPayResult(String orderId) {
        Config config =
                new RSAAutoCertificateConfig.Builder()
                        .merchantId(weixinPayProperties.getMerchantId())
                        .privateKey(weixinPayProperties.getPrivateKey())
                        .merchantSerialNumber(weixinPayProperties.getMerchantSerialNumber())
                        .apiV3Key(weixinPayProperties.getApiV3Key())
                        .build();
        NativePayService service = new NativePayService.Builder().config(config).build();
        QueryOrderByOutTradeNoRequest queryOrderByIdRequest = new QueryOrderByOutTradeNoRequest();
        queryOrderByIdRequest.setMchid(weixinPayProperties.getMerchantId());
        queryOrderByIdRequest.setOutTradeNo(orderId);
        Transaction transaction = service.queryOrderByOutTradeNo(queryOrderByIdRequest);
        return PayResultResponse.builder()
                .appId(transaction.getAppid())
                .outTradeNo(transaction.getOutTradeNo())
                .transactionId(transaction.getTransactionId())
                .buyerId(transaction.getPayer().getOpenid())
                .status(PayStatusEnum.ofWeixinPayStatus(transaction.getTradeState()))//这个需要统一处理
                .amount(transaction.getAmount().getTotal())
                .payAmount(transaction.getAmount().getPayerTotal())
                .currency(transaction.getAmount().getCurrency())
                .build();
    }

    @Override
    public PayResultResponse payNotify(String body, HttpServletRequest request) {
        String nonce = request.getHeader("Wechatpay-Nonce");
        String signature = request.getHeader("Wechatpay-Signature");
        String timestamp = request.getHeader("Wechatpay-Timestamp");
        RequestParam requestParam = new RequestParam.Builder()
                .serialNumber(weixinPayProperties.getMerchantSerialNumber())
                .nonce(nonce)
                .signature(signature)
                .timestamp(timestamp)
                .body(body)
                .build();
        NotificationConfig config =
                new RSAAutoCertificateConfig.Builder()
                        .merchantId(weixinPayProperties.getMerchantId())
                        .privateKey(weixinPayProperties.getPrivateKey())
                        .merchantSerialNumber(weixinPayProperties.getMerchantSerialNumber())
                        .apiV3Key(weixinPayProperties.getApiV3Key())
                        .build();
        NotificationParser parser = new NotificationParser(config);
        Transaction transaction = parser.parse(requestParam, Transaction.class);
        return PayResultResponse.builder()
                .appId(transaction.getAppid())
                .outTradeNo(transaction.getOutTradeNo())
                .transactionId(transaction.getTransactionId())
                .buyerId(transaction.getPayer().getOpenid())
                .status(PayStatusEnum.ofWeixinPayStatus(transaction.getTradeState()))//这个需要统一处理
                .amount(transaction.getAmount().getTotal())
                .payAmount(transaction.getAmount().getPayerTotal())
                .currency(transaction.getAmount().getCurrency())
                .build();
    }
}
