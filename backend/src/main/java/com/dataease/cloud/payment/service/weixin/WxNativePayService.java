package com.dataease.cloud.payment.service.weixin;

import com.dataease.cloud.payment.annotation.PayTypeAnnotation;
import com.dataease.cloud.payment.constant.PayProvider;
import com.dataease.cloud.payment.constant.PayStatusEnum;
import com.dataease.cloud.payment.entity.request.PayOrderRequest;
import com.dataease.cloud.payment.entity.response.PayOrderResponse;
import com.dataease.cloud.payment.entity.response.PayResultResponse;
import com.dataease.cloud.payment.properties.WeixinPayProperties;
import com.dataease.cloud.payment.service.AbstractPayService;
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
 * 微信Native支付
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/26 6:00 PM
 **/
@PayTypeAnnotation(PayProvider.WX_NATIVE)
public class WxNativePayService extends AbstractPayService<WeixinPayProperties> {

    public WxNativePayService(WeixinPayProperties properties) {
        super(properties);
    }

    @Override
    public PayOrderResponse createPayOrder(PayOrderRequest payOrderRequest) {
        Config config =
                new RSAAutoCertificateConfig.Builder()
                        .merchantId(properties.getMerchantId())
                        .privateKey(properties.getPrivateKey())
                        .merchantSerialNumber(properties.getMerchantSerialNumber())
                        .apiV3Key(properties.getApiV3Key())
                        .build();
        NativePayService service = new NativePayService.Builder().config(config).build();
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(payOrderRequest.getAmount());
        request.setAmount(amount);
        request.setAppid(properties.getAppId());
        request.setMchid(properties.getMerchantId());
        request.setDescription(payOrderRequest.getDescription());
        request.setNotifyUrl(properties.getNotifyUrl());
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
                        .merchantId(properties.getMerchantId())
                        .privateKey(properties.getPrivateKey())
                        .merchantSerialNumber(properties.getMerchantSerialNumber())
                        .apiV3Key(properties.getApiV3Key())
                        .build();
        NativePayService service = new NativePayService.Builder().config(config).build();
        QueryOrderByOutTradeNoRequest queryOrderByIdRequest = new QueryOrderByOutTradeNoRequest();
        queryOrderByIdRequest.setMchid(properties.getMerchantId());
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
                .serialNumber(properties.getMerchantSerialNumber())
                .nonce(nonce)
                .signature(signature)
                .timestamp(timestamp)
                .body(body)
                .build();
        NotificationConfig config =
                new RSAAutoCertificateConfig.Builder()
                        .merchantId(properties.getMerchantId())
                        .privateKey(properties.getPrivateKey())
                        .merchantSerialNumber(properties.getMerchantSerialNumber())
                        .apiV3Key(properties.getApiV3Key())
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
