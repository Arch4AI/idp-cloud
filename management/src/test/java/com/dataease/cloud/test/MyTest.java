package com.dataease.cloud.test;

import com.dataease.cloud.payment.constant.PayProvider;
import com.dataease.cloud.payment.factory.PayProviderFactory;
import com.dataease.cloud.payment.properties.WeixinPayProperties;
import com.dataease.cloud.payment.service.AbstractPayService;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * [描述当前类是什么作用]
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/15 5:19 PM
 **/
public class MyTest {

    public static void main(String[] args) throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
//        List<Module> securityModules = SecurityJackson2Modules.getModules(MyTest.class.getClassLoader());
//        objectMapper.registerModules(securityModules);
//        objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
//        objectMapper.registerModule(new CustomJackson2Module());
//        String data = "{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"java.security.Principal\":{\"@class\":\"com.dataease.cloud.security.password.PasswordAuthenticationToken\",\"authorities\":[\"java.util.Collections$UnmodifiableRandomAccessList\",[{\"@class\":\"org.springframework.security.core.authority.SimpleGrantedAuthority\",\"authority\":\"ROLE_ADMIN\"}]],\"details\":{\"@class\":\"org.springframework.security.web.authentication.WebAuthenticationDetails\",\"remoteAddress\":\"127.0.0.1\",\"sessionId\":null},\"authenticated\":true,\"principal\":{\"@class\":\"com.dataease.cloud.security.LocalUser\",\"userId\":\"bb027e46-0ded-49a5-930d-c57999c2267c\",\"userName\":\"admin\",\"nickName\":\"超级管理员\",\"gender\":1,\"phone\":\"13168334636\",\"email\":\"admin@cloud.io\",\"enabled\":true,\"createTime\":[\"java.util.Date\",1678695661000],\"updateTime\":[\"java.util.Date\",1678787279000],\"language\":\"ZH_CN\",\"authorities\":[\"java.util.HashSet\",[{\"@class\":\"org.springframework.security.core.authority.SimpleGrantedAuthority\",\"authority\":\"ROLE_ADMIN\"}]],\"loginType\":\"PASSWORD\",\"username\":\"bb027e46-0ded-49a5-930d-c57999c2267c\",\"accountNonExpired\":true,\"accountNonLocked\":true,\"credentialsNonExpired\":true},\"credentials\":null,\"name\":\"bb027e46-0ded-49a5-930d-c57999c2267c\"},\"org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest\":{\"@class\":\"org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest\",\"authorizationUri\":\"http://127.0.0.1:8090/oauth2/authorize\",\"authorizationGrantType\":{\"value\":\"authorization_code\"},\"responseType\":{\"value\":\"code\"},\"clientId\":\"idp_cloud\",\"redirectUri\":\"https://www.baidu.com\",\"scopes\":[\"java.util.Collections$UnmodifiableSet\",[\"profile\"]],\"state\":null,\"additionalParameters\":{\"@class\":\"java.util.Collections$UnmodifiableMap\"},\"authorizationRequestUri\":\"http://127.0.0.1:8090/oauth2/authorize?response_type=code&client_id=idp_cloud&scope=profile&redirect_uri=https://www.baidu.com\",\"attributes\":{\"@class\":\"java.util.Collections$UnmodifiableMap\"}},\"state\":\"gtelGsrKFmdKyxHWy0Q06lurUmGD0iuVYE6m-Wqqjl4=\"}";
//        Map<String, Object> objectMap = objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {});
//
//        System.out.println(objectMap.keySet().size());
//        String puk = "panjiannneng";
//        String prk = "shitouzhu";
//        System.out.println(AddressUtils.getRealAddressByIP("120.196.86.13"));
//        generator();
//        System.out.println(JdbcTokenRepositoryImpl.CREATE_TABLE_SQL);
//        AntPathRequestMatcher matcher = new AntPathRequestMatcher("/user/{userId}/add");
//        HttpServletRequest httpServletRequest = new HttpServletRequestImpl();

//        String key = "spring:session:sessions:2407437c-4a3c-49da-a6ee-81b10a174ced";
//        String baseKey = Constants.redisNamespace+":sessions:";
//        System.out.println(key.substring(baseKey.length()));
//        System.out.println(I18nService.getMessage("aaaaa"));
        //返回信息
//        System.out.println(LanguageEnum.ZH_CN.name());
//        System.out.println(Locale.US.toString());
//        System.out.println(AddressUtils.getRealAddressByIP("120.196.86.13"));
//        Map<String,Object> map = new HashMap<>(){{
//            put("a",false);
//            put("b",false);
//        }};
//       String str = ObjectMapperUtils.simpleObj   ectMapper.writeValueAsString(map);
//       JsonNode jsonNode = ObjectMapperUtils.simpleObjectMapper.readTree(str);
//       System.out.println(jsonNode.get("a").toString());
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("aaa","bbbbb");
//        jsonObject.put("asadas","asadsad");
//        System.out.println(jsonObject.toString());
//        File file = new File("/tmp/aa.txt");
//        IoUtil.copy(FileUtil.getInputStream(new File("/Users/liuchangchang/Downloads/1.sql")),FileUtil.getOutputStream(file));
//        alipay();
//        String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC/NRe7WanqAd7soby7ipQmhNzkctnC91kLLztAqpYIAELlXrARwDnawf3QU/6in0MfYBv+hDUhnSVeWH3+9bqj6KXGxMtyM3hSO/AYp/SPf9YHEX4KXHfphJQ9X46i5WzmMTSnUxI8uxtCz03sIVjlmc4McLPfwSEwW2Cjo/umzM7NTmsSC1WRM3AY4+Dt5eHVevEqxuOYD9MnFHZuVNcpUOQKFMRtUhShSXk0dV561QvCfPnypcHMNr9bLcwiIS4R/cCkyS8EYbQ52QxyHo5gH4vsmJ4kDfqtzXX1GbVsTXFANNnvgzF3W7K+NOGgMM9gLln5sV09zxxIDzd6ANwvAgMBAAECggEAb/d+KyaJWk+4I+deJpAyyw+YbmpW3FWNngANeYOrEYpdogU9o1dmSZS5tYnKHZ6XZsMYAYWXu1RTcpjtRyTMI3lzwh6xZ/F97uHMahPIIVPhmAgFP7oAOg155SqU0wz4JSABTk5V6pxCjW/pmHG6pdDYGO7SVh+4+KE5yPmYvoZDAyTu5BIOY1EU7BVKQe/d4vxo2WAOK/kxlfv4NUKHCKEfstO1Z6YAwHBoW5+9Lg04YEY5NRSelYksOc1nWQTyuYRkSeipXbII0AtphujTCdPNrx+xck4i6N0LNzuSFsWBDPi9SVd+BhH3AdbJNiOKFjMbiHbRGfsQyYOjndz6EQKBgQDkFVHEbHi0G/fwWAz+HNp6E7iHsZNAoOBPXCl3lRgEjLtuRgy4lB7TyDWZ8GDAfuVrNpWWI6wmUBMqRXlvCuV0H0HE0BTnFQsbQ9ViOTpD/HlTsBM5TKrgiXH6qub1XD4WUznw+RTJsaLtrx+dm0w231a91ya3Q32e/IJMIM4SBwKBgQDWnFE/gUV5icgWm/S1UIG5WMLfletnK2hkTlHSjcmZMIZUKnvBCNpKattyRGFMNbpdn4SuTe6mUt6FyE6s0NDZyL3hJW7TAKOpB7QSzX+Y57/dbd48lXvXmHJ7RJpdDfK2aSET69AkNv1bY+iZPnZhrBSFgRUiWojq4jDxgIm6mQKBgAWRf62L+GZtSGBVxftuOOdCE/88Fpw9UTyew2Rn/24dAXZvv1/CPf5tpOFdPP92GaPEcWJ4/Hry+YTOrWhIm9F58eEcM2Zw3RSNU2hSfVmrkEq6wb1kYceZk27LPVhAKEPjREbdzeQeshubauouIG42haec2RUgULDQ+V1ib6WBAoGBALEb/f0/rNs5dtQZzqCXmQciFmi4a7YP363v6JywALCYmDL7gHf021lQyFuLzRQ8vtmm3LOQgyPl/dhAT5VVGxawEOuuncAqpeXIArcFuoiwsOmHU0GCR9rL56aNHisvaJ9bMT4kfPh2HS5/zCQzpTTD84ed2JgqSRY2pxeW8ZL5AoGBAK/E3A7rjErYS+3DshfSKW+AQybsKfj4u6BDk1Vft2eYZ4v+77yKq2kd0gitTt3hCJDalfoU6k05EF4p6zkO+O5pprDLZAjzl0NOQ/fnkfeGFRAkMSyMjP8N9D3ZOR0i0LELXkudWqhTB8FelLZDWf82LBEEvITegfwQy8oIthPx";
//        String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAggpFfU61Vx3C6Ba7cMVn38kXOaMEKQkAA4TySrVrkLWFQEmqXWU8u7uvR3OhcjU04oeASxSRGTScrjgdWIRjvNlT0A20npWtBEpXpzwrLw1f3GGwCRKT6ZYKJf0h8c0+kVUXhx6PwG2JY6j2TFiAHE7iSX9/p/7hpQd0kFDPfaGPkfSeCXQXdSN2Oaf/kOE5uYnsla9D1jR1jkthtvtayiv8Tjas9Cr8WHnFchTp00Exa7WsT19PVcX/b6tigbBG2kRkAg7jqFc/lVPMh4T2OtLeglCJTLVvDwzMa+jCLpOLdQZLZf23TQM+921GywPv00LgQ03fvb3OJU7yQdrm3QIDAQAB";
//        AliPayProperties aliPayProperties = new AliPayProperties();
//        aliPayProperties.setAlipayPublicKey(alipayPublicKey);
//        aliPayProperties.setAppId("2021000122684081");
//        aliPayProperties.setPrivateKey(privateKey);
//        AliPayEasyService aliPayEasyService = new AliPayEasyService(aliPayProperties);
//        PayOrderRequest request = PayOrderRequest.builder()
//                .orderId(IdUtil.simpleUUID())
//                .description("测试产品")
//                .amount(1000)
//                .build();
//        aliPayEasyService.createPayOrder(request);
        WeixinPayProperties weixinPayProperties = new WeixinPayProperties();
        weixinPayProperties.setPayProvider(PayProvider.WX_NATIVE);
        AbstractPayService abstractPayService = PayProviderFactory.getPayService(weixinPayProperties);
        System.out.println(abstractPayService.queryPayResult("12345"));
    }

    private static void generator() throws Exception {
        File configFile = new File("/Users/liuchangchang/Code/cloud/idp-cloud/management/src/main/resources/mybatis-generator.xml");
        List<String> warnings = new ArrayList<>();
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(true);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(new ProgressCallback() {
            @Override
            public void done() {
                System.out.println("aaaa");
                ProgressCallback.super.done();
            }
        });
        System.out.println("代码生成完毕>>>>>>>>>>>>");
    }

    private static void alipay() throws Exception {
        String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC/NRe7WanqAd7soby7ipQmhNzkctnC91kLLztAqpYIAELlXrARwDnawf3QU/6in0MfYBv+hDUhnSVeWH3+9bqj6KXGxMtyM3hSO/AYp/SPf9YHEX4KXHfphJQ9X46i5WzmMTSnUxI8uxtCz03sIVjlmc4McLPfwSEwW2Cjo/umzM7NTmsSC1WRM3AY4+Dt5eHVevEqxuOYD9MnFHZuVNcpUOQKFMRtUhShSXk0dV561QvCfPnypcHMNr9bLcwiIS4R/cCkyS8EYbQ52QxyHo5gH4vsmJ4kDfqtzXX1GbVsTXFANNnvgzF3W7K+NOGgMM9gLln5sV09zxxIDzd6ANwvAgMBAAECggEAb/d+KyaJWk+4I+deJpAyyw+YbmpW3FWNngANeYOrEYpdogU9o1dmSZS5tYnKHZ6XZsMYAYWXu1RTcpjtRyTMI3lzwh6xZ/F97uHMahPIIVPhmAgFP7oAOg155SqU0wz4JSABTk5V6pxCjW/pmHG6pdDYGO7SVh+4+KE5yPmYvoZDAyTu5BIOY1EU7BVKQe/d4vxo2WAOK/kxlfv4NUKHCKEfstO1Z6YAwHBoW5+9Lg04YEY5NRSelYksOc1nWQTyuYRkSeipXbII0AtphujTCdPNrx+xck4i6N0LNzuSFsWBDPi9SVd+BhH3AdbJNiOKFjMbiHbRGfsQyYOjndz6EQKBgQDkFVHEbHi0G/fwWAz+HNp6E7iHsZNAoOBPXCl3lRgEjLtuRgy4lB7TyDWZ8GDAfuVrNpWWI6wmUBMqRXlvCuV0H0HE0BTnFQsbQ9ViOTpD/HlTsBM5TKrgiXH6qub1XD4WUznw+RTJsaLtrx+dm0w231a91ya3Q32e/IJMIM4SBwKBgQDWnFE/gUV5icgWm/S1UIG5WMLfletnK2hkTlHSjcmZMIZUKnvBCNpKattyRGFMNbpdn4SuTe6mUt6FyE6s0NDZyL3hJW7TAKOpB7QSzX+Y57/dbd48lXvXmHJ7RJpdDfK2aSET69AkNv1bY+iZPnZhrBSFgRUiWojq4jDxgIm6mQKBgAWRf62L+GZtSGBVxftuOOdCE/88Fpw9UTyew2Rn/24dAXZvv1/CPf5tpOFdPP92GaPEcWJ4/Hry+YTOrWhIm9F58eEcM2Zw3RSNU2hSfVmrkEq6wb1kYceZk27LPVhAKEPjREbdzeQeshubauouIG42haec2RUgULDQ+V1ib6WBAoGBALEb/f0/rNs5dtQZzqCXmQciFmi4a7YP363v6JywALCYmDL7gHf021lQyFuLzRQ8vtmm3LOQgyPl/dhAT5VVGxawEOuuncAqpeXIArcFuoiwsOmHU0GCR9rL56aNHisvaJ9bMT4kfPh2HS5/zCQzpTTD84ed2JgqSRY2pxeW8ZL5AoGBAK/E3A7rjErYS+3DshfSKW+AQybsKfj4u6BDk1Vft2eYZ4v+77yKq2kd0gitTt3hCJDalfoU6k05EF4p6zkO+O5pprDLZAjzl0NOQ/fnkfeGFRAkMSyMjP8N9D3ZOR0i0LELXkudWqhTB8FelLZDWf82LBEEvITegfwQy8oIthPx";
        String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAggpFfU61Vx3C6Ba7cMVn38kXOaMEKQkAA4TySrVrkLWFQEmqXWU8u7uvR3OhcjU04oeASxSRGTScrjgdWIRjvNlT0A20npWtBEpXpzwrLw1f3GGwCRKT6ZYKJf0h8c0+kVUXhx6PwG2JY6j2TFiAHE7iSX9/p/7hpQd0kFDPfaGPkfSeCXQXdSN2Oaf/kOE5uYnsla9D1jR1jkthtvtayiv8Tjas9Cr8WHnFchTp00Exa7WsT19PVcX/b6tigbBG2kRkAg7jqFc/lVPMh4T2OtLeglCJTLVvDwzMa+jCLpOLdQZLZf23TQM+921GywPv00LgQ03fvb3OJU7yQdrm3QIDAQAB";
//        AlipayConfig alipayConfig = new AlipayConfig();
//        alipayConfig.setServerUrl("https://openapi.alipaydev.com/gateway.do");
//        alipayConfig.setAppId("2021000122684081");
//        alipayConfig.setPrivateKey(privateKey);
//        alipayConfig.setFormat("json");
//        alipayConfig.setAlipayPublicKey(alipayPublicKey);
//        alipayConfig.setCharset("UTF8");
//        alipayConfig.setSignType("RSA2");
//        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
//        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
//        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
//        model.setOutTradeNo("20150320010101001");
//        model.setQrPayMode("2");
//        model.setTotalAmount("88.88");
//        model.setSubject("Iphone6 16G");
//        model.setProductCode("FAST_INSTANT_TRADE_PAY");
//        request.setBizModel(model);
//        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
//        System.out.println(response.getBody());
//        if (response.isSuccess()) {
//            System.out.println("调用成功");
//        } else {
//            System.out.println("调用失败");
//        }
    }
}
