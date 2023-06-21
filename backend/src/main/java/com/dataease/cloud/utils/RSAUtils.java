package com.dataease.cloud.utils;

import cn.hutool.core.codec.Base64;
import com.dataease.cloud.security.jose.Jwks;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 获取固定的RSA加密
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/16 5:32 PM
 **/
@Slf4j
public class RSAUtils {

    private static final String RSA_ALGORITHM = "RSA";
    private static final String key = "cloud_rsa";
    private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzq4knG0kv5n1jD0+LpzvA8kqdOZ0iYS/CbH1/MAcB5jfw7kg09wPlNTUpY3cAUsYabeBiVzYDH7nAR2qmaYIJp292VBssT+zOOJQayNQudejuKhhUkigDV2ThmgvsJ74M3AxciRAtjC96aoM5C1QoZY9fVBOk0F5a82ztmk7tWyljvyvkBAFK5B3CqZlhLpgRaZW41Efq7YZJGXDner1dtG2qG/an4mOcgUo+39F8vYtZPE0sMR7+ZmAyJ9WU3TLS3rwLIvDscOlCwAS2uU6Yv0xc8vrlOiACpig9CPIv0DFvjzf83A7St9BlS1OrGUjD5mV+lz9GkhIEkmnZWGamwIDAQAB";
    private static final String PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDOriScbSS/mfWMPT4unO8DySp05nSJhL8JsfX8wBwHmN/DuSDT3A+U1NSljdwBSxhpt4GJXNgMfucBHaqZpggmnb3ZUGyxP7M44lBrI1C516O4qGFSSKANXZOGaC+wnvgzcDFyJEC2ML3pqgzkLVChlj19UE6TQXlrzbO2aTu1bKWO/K+QEAUrkHcKpmWEumBFplbjUR+rthkkZcOd6vV20baob9qfiY5yBSj7f0Xy9i1k8TSwxHv5mYDIn1ZTdMtLevAsi8Oxw6ULABLa5Tpi/TFzy+uU6IAKmKD0I8i/QMW+PN/zcDtK30GVLU6sZSMPmZX6XP0aSEgSSadlYZqbAgMBAAECggEAGLiLSCs6hYZl8I0Uu8uT6n6Bywi+RgKa57nMWUZ7M8zCcFfbiCu0BhJs5ZPHDbU3PtHZtx3j+PP8L83Qwt8GmOPY5Ojcj1jgrXxNr9BCDsXZtIkYrEMOjdAJs2kRJzg51MvlxiggQLUqH2GdIsppcLfBNBTKc+JAIo0F+AVfRG762aidArCX185zh/rwY7SJfIn6f6jzvazhpXkz15mMJdsOem7C7tW+Zh3hZPQWVyfe1HzUKutYGQ9a/u6qSVRKLtpEgGmQc3kBsX/E3SI5SYJBF+9N3AUMF+d2+9c2aKfTkwdf8pZ5FhfcOiJ4OHwnDUy8Q3qEw4b6sN2AyzkKPQKBgQDlO1k0MvSSfsBlNe5rhiaedzNG90tn1uJhYEOyBKcmYbI7d16WtqTB7DzW69HF5L/VRU8Y8y7aVqAcfpSSr26BHRWKSWkmIguZpY8ig9uaGhrNXVWZk4xGCivDNk4cSNL4FGFlEA1zVZfmf5alian4qGtSof/8isTiGwIN0gVHPQKBgQDm0KNS7yZqHG7x4gGGxGN7fdAQtk050Q0QxkyDhBb+gUYdkpkSepSVW2ZUgU/DK6WBvsq4Bx/SpJkEM0IFDJ1Q4Hyd+g8VxiUNHbWS7Nj2hAr1q6svE/ZFMavnsprVEO/OmruiRl/myPgYb2QxTeoa0N5imkpYuuQx1/GKhLVGtwKBgBUlun8ETGfPZbRwm0Q0Jg/sc0CSw9QakNvm45fisgDqdmTiCEnjI6P4RUyWiM224rOsJzXoCWR/JVo7mz1FDe6/XiNZzmeEMInGKkRG4dV/nwVfHOyEHia//XWO5vnZBg1449tM9dC0DsVm8+DlqFaWQwBmjZUgwDUTxMa9n1sJAoGBAN/K1UCZPf7OWMfQITjA+9AFmZJKn6/6QJfOHOu7S+wx/YC43g/rqFr0RtCSnHqYvB2Dltw9lB5Rl0daR53LsFB1iH5HsQ6kktIXxzCQ0oCaYGH4s76utS47zfiV1MfXZxxELL9XSq7ilqJihpvDdsPPVn/2JzXG53BodvqeV4EbAoGAE13+kFGRMYU6OOVK3ULppLCfnM+YFvHtkYv0pccGb5/ElkGN5CUHvGYTfVAxXDRaVVTI6UCNGdC28R8lLGXADJ6AFOElx+0vWtKPo2YrgLOAebRnH3zksYgxZUUTl2tm89Wf49vsNDw7qvm8Aeo4Uim3hQjz76+8eENovjz8dbA=";


    /**
     * 生成RSA-key 固定的
     *
     * @return
     */
    public static RSAKey generateRsa() {
        try {
            RSAPublicKey publicKey = getPublicKey(PUBLIC_KEY);
            RSAPrivateKey privateKey = getPrivateKey(PRIVATE_KEY);
            return new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID(key)
                    .build();
        } catch (Exception e) {
            log.error("generate fix rsa key:", e);
        }
        return Jwks.generateRsa();
    }


    /**
     * 得到公钥
     *
     * @param publicKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    private static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decode(publicKey));
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        return key;
    }

    /**
     * 得到私钥
     *
     * @param privateKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    private static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decode(privateKey));
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return key;
    }
}
