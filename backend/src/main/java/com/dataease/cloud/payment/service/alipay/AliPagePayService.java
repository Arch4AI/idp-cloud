package com.dataease.cloud.payment.service.alipay;

import cn.hutool.core.util.NumberUtil;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
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

    private static final String signType = "RSA2";

    public AliPagePayService(AliPayProperties properties) {
        super(properties);
    }

    @Override
    public PayOrderResponse createPayOrder(PayOrderRequest payOrderRequest) {
        Factory.setOptions(getOptions());
        try {
            //easy aili payMent
            AlipayTradePagePayResponse response = Factory.Payment.Page()
                    .optional("qr_pay_mode","0")
                    .pay(payOrderRequest.getDescription(), payOrderRequest.getOrderId(), String.valueOf(NumberUtil.div(payOrderRequest.getAmount(), 100, 2)), payOrderRequest.getReturnUrl());
            return PayOrderResponse.builder()
                    .orderId(payOrderRequest.getOrderId())
                    .htmlContent(response.getBody())
                    .build();
        } catch (Exception e) {
            CloudException.throwException(e);
        }
        return null;
    }

    @Override
    public PayResultResponse queryPayResult(String orderId) {
        Factory.setOptions(getOptions());
        try {
            AlipayTradeQueryResponse response = Factory.Payment.Common().query(orderId);
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
        } catch (Exception e) {
            CloudException.throwException(e);
        }
        return null;
    }

    /**
     * Map<String, String> parameters = new HashMap<>();
     * parameters.put("charset", "UTF-8");
     * parameters.put("sign", "GM0CbuqaEivqgb......");
     * parameters.put("app_id", "2018091261392200");
     * parameters.put("sign_type", "RSA2");
     * parameters.put("isv_ticket", "");
     * parameters.put("timestamp", "2020-03-25 16:27:08");
     * //... ... 接收到的所有参数放入一个Map中
     * Factory.Payment.Common().verifyNotify(parameters);
     *
     * @param body
     * @param request
     * @return
     */
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
            Factory.setOptions(getOptions());
            boolean signVerified = Factory.Payment.Common().verifyNotify(params);
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

    /**
     * 请求地址
     *
     * @return
     */
    private Config getOptions() {
        Config config = new Config();
        config.protocol = "https";
        config.gatewayHost = properties.getServerDomain();
        config.signType = signType;
        config.appId = properties.getAppId();
        config.ignoreSSL = true;
        // 为避免私钥随源码泄露，推荐从文件中读取私钥字符串而不是写入源码中
        config.merchantPrivateKey = properties.getPrivateKey();
        //注：证书文件路径支持设置为文件系统中的路径或CLASS_PATH中的路径，优先从文件系统中加载，加载失败后会继续尝试从CLASS_PATH中加载
//        config.merchantCertPath = "<-- 请填写您的应用公钥证书文件路径，例如：/foo/appCertPublicKey_2019051064521003.crt -->";
//        config.alipayCertPath = "<-- 请填写您的支付宝公钥证书文件路径，例如：/foo/alipayCertPublicKey_RSA2.crt -->";
//        config.alipayRootCertPath = "<-- 请填写您的支付宝根证书文件路径，例如：/foo/alipayRootCert.crt -->";
        //注：如果采用非证书模式，则无需赋值上面的三个证书路径，改为赋值如下的支付宝公钥字符串即可
        config.alipayPublicKey = properties.getPublicKey();
        //可设置异步通知接收服务地址（可选）
        config.notifyUrl = properties.getNotifyUrl();
        //可设置AES密钥，调用AES加解密相关接口时需要（可选）
//        config.encryptKey = "<-- 请填写您的AES密钥，例如：aa4BtZ4tspm2wnXLb1ThQA== -->";
        return config;
    }
}
