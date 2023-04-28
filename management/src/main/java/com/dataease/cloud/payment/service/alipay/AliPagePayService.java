package com.dataease.cloud.payment.service.alipay;

import cn.hutool.core.util.NumberUtil;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.dataease.cloud.exception.CloudException;
import com.dataease.cloud.i18n.I18nService;
import com.dataease.cloud.payment.annotation.PayTypeAnnotation;
import com.dataease.cloud.payment.constant.PayProvider;
import com.dataease.cloud.payment.constant.PayStatusEnum;
import com.dataease.cloud.payment.entity.request.PayOrderRequest;
import com.dataease.cloud.payment.entity.response.PayOrderResponse;
import com.dataease.cloud.payment.entity.response.PayResultResponse;
import com.dataease.cloud.payment.properties.AliPayProperties;
import com.dataease.cloud.payment.service.AbstractPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 支付宝电脑网页支付
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/26 6:43 PM
 **/
@PayTypeAnnotation(PayProvider.ALI_PAGE)
public class AliPagePayService extends AbstractPayService<AliPayProperties> {

    private static final String charset = "UTF8";
    private static final String format = "json";
    private static final String signType = "RSA2 ";
    private static final String productCode = "FAST_INSTANT_TRADE_PAY";//支付宝这个是写死的

    public AliPagePayService(AliPayProperties properties) {
        super(properties);
    }

    @Override
    public PayOrderResponse createPayOrder(PayOrderRequest payOrderRequest) {
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl(properties.getServerUrl());
        alipayConfig.setAppId(properties.getAppId());
        alipayConfig.setPrivateKey(properties.getPrivateKey());
        alipayConfig.setFormat(format);
        alipayConfig.setAlipayPublicKey(properties.getPublicKey());
        alipayConfig.setCharset(charset);
        alipayConfig.setSignType(signType);
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            AlipayTradePagePayModel model = new AlipayTradePagePayModel();
            model.setOutTradeNo(payOrderRequest.getOrderId());
            model.setQrPayMode("1");
            model.setTotalAmount(String.valueOf(NumberUtil.div(payOrderRequest.getAmount(), 100, 2)));
            model.setSubject(payOrderRequest.getDescription());
            model.setProductCode(productCode);
            request.setBizModel(model);
            //支付完后跳转回去的地址 如果不填默认不会跳转回自己的应用 最好是回到对应的订单页面
            request.setReturnUrl(payOrderRequest.getReturnUrl());
            if (StringUtils.hasText(properties.getNotifyUrl())) {
                request.setNotifyUrl(properties.getNotifyUrl());
            }
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if (response.isSuccess()) {
                return PayOrderResponse.builder()
                        .orderId(payOrderRequest.getOrderId())
                        .htmlContent(response.getBody())
                        .build();
            } else {
                CloudException.throwException(response.getSubMsg());
            }
        } catch (Exception e) {
            CloudException.throwException(e);
        }
        return null;
    }

    @Override
    public PayResultResponse queryPayResult(String orderId) {
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl(properties.getServerUrl());
        alipayConfig.setAppId(properties.getAppId());
        alipayConfig.setPrivateKey(properties.getPrivateKey());
        alipayConfig.setFormat(format);
        alipayConfig.setAlipayPublicKey(properties.getPublicKey());
        alipayConfig.setCharset(charset);
        alipayConfig.setSignType(signType);
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            AlipayTradeQueryModel model = new AlipayTradeQueryModel();
            model.setOutTradeNo(orderId);
            request.setBizModel(model);
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                return PayResultResponse.builder()
                        .appId(properties.getAppId())
                        .outTradeNo(response.getOutTradeNo())
                        .transactionId(response.getTradeNo())
                        .buyerId(response.getBuyerLogonId())
                        .status(PayStatusEnum.ofAlipayStatus(response.getTradeStatus()))//这个需要统一处理
                        .amount(NumberUtil.mul(response.getTotalAmount(), "100").intValue())//转成分
                        .payAmount(NumberUtil.mul(response.getPayAmount(), "100").intValue())//转成分
                        .currency(response.getPayCurrency())
                        .subject(response.getSubject())
                        .build();
            } else {
                CloudException.throwException(response.getSubMsg());
            }
        } catch (Exception e) {
            CloudException.throwException(e);
        }
        return null;
    }

    @Override
    public PayResultResponse payNotify(String body, HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        try {
            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用
                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
                params.put(name, valueStr);
            }
            boolean signVerified = AlipaySignature.rsaCheckV1(params, properties.getPublicKey(), charset, signType);
            if (signVerified) {
                String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
                // 根据orderId查询信息
                return queryPayResult(out_trade_no);
            } else {
                CloudException.throwException(I18nService.getMessage("sign_verified_error"));
            }
        } catch (Exception e) {
            CloudException.throwException(e);
        }
        return null;
    }
}
