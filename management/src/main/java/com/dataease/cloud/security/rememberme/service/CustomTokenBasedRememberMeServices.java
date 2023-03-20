package com.dataease.cloud.security.rememberme.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.util.Assert;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Date;

/**
 * 重写记住我的方法
 *
 * 因为默认有记住我 所以每次都会进来这里面
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/16 6:03 PM
 **/
public class CustomTokenBasedRememberMeServices extends TokenBasedRememberMeServices {

    private static final RememberMeTokenAlgorithm DEFAULT_MATCHING_ALGORITHM = RememberMeTokenAlgorithm.SHA256;

    private RememberMeTokenAlgorithm matchingAlgorithm = DEFAULT_MATCHING_ALGORITHM;
    private int maxInactiveInterval;

    public CustomTokenBasedRememberMeServices(String key, UserDetailsService userDetailsService,int maxInactiveInterval) {
        super(key, userDetailsService);
        this.maxInactiveInterval = maxInactiveInterval;
    }

    public CustomTokenBasedRememberMeServices(String key, UserDetailsService userDetailsService, RememberMeTokenAlgorithm encodingAlgorithm) {
        super(key, userDetailsService, encodingAlgorithm);
    }


    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request,
                                                 HttpServletResponse response) {
        if (!isValidCookieTokensLength(cookieTokens)) {
            throw new InvalidCookieException(
                    "Cookie token did not contain 3 or 4 tokens, but contained '" + Arrays.asList(cookieTokens) + "'");
        }
        long tokenExpiryTime = getTokenExpiryTime(cookieTokens);
        if (isTokenExpired(tokenExpiryTime)) {
            throw new InvalidCookieException("Cookie token[1] has expired (expired on '" + new Date(tokenExpiryTime)
                    + "'; current time is '" + new Date() + "')");
        }
        UserDetails userDetails = getUserDetailsService().loadUserByUsername(cookieTokens[0]);
        Assert.notNull(userDetails, () -> "UserDetailsService " + getUserDetailsService()
                + " returned null for username " + cookieTokens[0] + ". " + "This is an interface contract violation");
        String actualTokenSignature = cookieTokens[2];
        RememberMeTokenAlgorithm actualAlgorithm = this.matchingAlgorithm;
        // signature
        if (cookieTokens.length == 4) {
            actualTokenSignature = cookieTokens[3];
            actualAlgorithm = RememberMeTokenAlgorithm.valueOf(cookieTokens[2]);
        }
        String expectedTokenSignature = makeTokenSignature(tokenExpiryTime, userDetails.getUsername(),
                userDetails.getPassword(), actualAlgorithm);
        if (!equals(expectedTokenSignature, actualTokenSignature)) {
            throw new InvalidCookieException("Cookie contained signature '" + actualTokenSignature + "' but expected '"
                    + expectedTokenSignature + "'");
        }
        // 设置一下http的connecttime
        request.getSession().setMaxInactiveInterval(maxInactiveInterval);
        return userDetails;
    }

    private static boolean equals(String expected, String actual) {
        byte[] expectedBytes = bytesUtf8(expected);
        byte[] actualBytes = bytesUtf8(actual);
        return MessageDigest.isEqual(expectedBytes, actualBytes);
    }

    private static byte[] bytesUtf8(String s) {
        return (s != null) ? Utf8.encode(s) : null;
    }

    private boolean isValidCookieTokensLength(String[] cookieTokens) {
        return cookieTokens.length == 3 || cookieTokens.length == 4;
    }

    private long getTokenExpiryTime(String[] cookieTokens) {
        try {
            return new Long(cookieTokens[1]);
        }
        catch (NumberFormatException nfe) {
            throw new InvalidCookieException(
                    "Cookie token[1] did not contain a valid number (contained '" + cookieTokens[1] + "')");
        }
    }


    @Override
    public void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
        super.onLoginSuccess(request, response, successfulAuthentication);
    }
}
