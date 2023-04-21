package com.dataease.cloud.i18n;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.text.MessageFormat;

/**
 * 国际化
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/21 1:50 PM
 **/
public class I18nService {

    private static MessageSource messageSource;

    public I18nService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * key获取国际化信息
     * 可以带参数
     * @param msgKey key
     * @param args 参数
     * @return
     */
    public static String getMessage(String msgKey, Object... args) {
        if(messageSource == null){
            return MessageFormat.format(msgKey,args);
        }
        return messageSource.getMessage(msgKey, args, msgKey, LocaleContextHolder.getLocale());
    }
}
