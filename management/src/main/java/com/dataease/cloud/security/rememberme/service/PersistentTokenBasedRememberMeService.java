package com.dataease.cloud.security.rememberme.service;

import com.dataease.cloud.base.domain.CloudRememberMeToken;
import com.dataease.cloud.service.RememberMeTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.*;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;

/**
 * 记住我持久化的service
 * 参考{@link PersistentTokenBasedRememberMeServices}
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/12 4:48 PM
 **/
public class PersistentTokenBasedRememberMeService extends AbstractRememberMeServices {

    private RememberMeTokenService tokenRepository;

    private SecureRandom random;

    public static final int DEFAULT_SERIES_LENGTH = 16;

    public static final int DEFAULT_TOKEN_LENGTH = 16;

    private int seriesLength = DEFAULT_SERIES_LENGTH;

    private int tokenLength = DEFAULT_TOKEN_LENGTH;

    public PersistentTokenBasedRememberMeService(String key, UserDetailsService userDetailsService,
                                                 RememberMeTokenService tokenRepository) {
        super(key, userDetailsService);
        this.random = new SecureRandom();
        this.tokenRepository = tokenRepository;
    }

    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request,
                                                 HttpServletResponse response) {
        if (cookieTokens.length != 2) {
            throw new InvalidCookieException("Cookie token did not contain " + 2 + " tokens, but contained '"
                    + Arrays.asList(cookieTokens) + "'");
        }
        String presentedSeries = cookieTokens[0];
        String presentedToken = cookieTokens[1];
        CloudRememberMeToken token = this.tokenRepository.getTokenForSeries(presentedSeries);
        if (token == null) {
            throw new RememberMeAuthenticationException("No persistent token found for series id: " + presentedSeries);
        }
        // 判断2个token值不一致 直接就移除掉原来的那个
        if (!presentedToken.equals(token.getToken())) {

            this.tokenRepository.removeBySeries(token.getSeries());
            throw new CookieTheftException(this.messages.getMessage(
                    "PersistentTokenBasedRememberMeServices.cookieStolen",
                    "Invalid remember-me token (Series/token) mismatch. Implies previous cookie theft attack."));
        }
        if (token.getLastUsed().getTime() + getTokenValiditySeconds() * 1000L < System.currentTimeMillis()) {
            this.tokenRepository.removeBySeries(token.getSeries());
            throw new RememberMeAuthenticationException("Remember-me login has expired");
        }

        this.logger.debug(LogMessage.format("Refreshing persistent login token for user '%s', series '%s'",
                token.getUserId(), token.getSeries()));
        CloudRememberMeToken newToken = new CloudRememberMeToken();
        newToken.setToken(generateTokenData());
        newToken.setSeries(token.getSeries());
        newToken.setUserId(token.getUserId());
        newToken.setLastUsed(Calendar.getInstance().getTime());
        newToken.setSessionId(request.getSession().getId());
        try {
            this.tokenRepository.updateToken(newToken);
            addCookie(newToken, request, response);
        }
        catch (Exception ex) {
            this.logger.error("Failed to update token: ", ex);
            throw new RememberMeAuthenticationException("Autologin failed due to data access problem");
        }
        return getUserDetailsService().loadUserByUsername(token.getUserId());
    }

    /**
     * Creates a new persistent login token with a new series number, stores the data in
     * the persistent token repository and adds the corresponding cookie to the response.
     *
     */
    @Override
    protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response,
                                  Authentication successfulAuthentication) {
        String username = successfulAuthentication.getName();
        this.logger.debug(LogMessage.format("Creating new persistent login for user %s", username));
        CloudRememberMeToken rememberMeToken = new CloudRememberMeToken();
        rememberMeToken.setToken(generateTokenData());
        rememberMeToken.setSeries(generateSeriesData());
        rememberMeToken.setUserId(username);
        rememberMeToken.setLastUsed(Calendar.getInstance().getTime());
        rememberMeToken.setSessionId(request.getSession().getId());
        try {
            this.tokenRepository.createNewToken(rememberMeToken);
            addCookie(rememberMeToken, request, response);
        }
        catch (Exception ex) {
            this.logger.error("Failed to save persistent token ", ex);
        }
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        super.logout(request, response, authentication);
        if (authentication != null && StringUtils.hasText(request.getRequestedSessionId())) {
            this.tokenRepository.removeBySessionId(request.getRequestedSessionId());
        }
    }

    protected String generateSeriesData() {
        byte[] newSeries = new byte[this.seriesLength];
        this.random.nextBytes(newSeries);
        return new String(Base64.getEncoder().encode(newSeries));
    }

    protected String generateTokenData() {
        byte[] newToken = new byte[this.tokenLength];
        this.random.nextBytes(newToken);
        return new String(Base64.getEncoder().encode(newToken));
    }

    private void addCookie(CloudRememberMeToken token, HttpServletRequest request, HttpServletResponse response) {
        setCookie(new String[] { token.getSeries(), token.getToken() }, getTokenValiditySeconds(), request,
                response);
    }

    public void setSeriesLength(int seriesLength) {
        this.seriesLength = seriesLength;
    }

    public void setTokenLength(int tokenLength) {
        this.tokenLength = tokenLength;
    }

    @Override
    public void setTokenValiditySeconds(int tokenValiditySeconds) {
        Assert.isTrue(tokenValiditySeconds > 0, "tokenValiditySeconds must be positive for this implementation");
        super.setTokenValiditySeconds(tokenValiditySeconds);
    }
}
