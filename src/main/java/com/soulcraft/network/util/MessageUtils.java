package com.soulcraft.network.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

/**
 * <p>
 * 国际化工具类
 * </p>
 *
 * @author Scott
 * @since 2022-03-10
 */
@Slf4j
public class MessageUtils {

    private static final MessageSource messageSource = SpringApplicationContextUtil.getBean(MessageSource.class);
    private static final String MESSAGE_KEY_ERROR_MESSAGES = "app.ErrorMessages";

    /**
     * 获取国际化消息
     *
     * @param code 消息Key
     * @param args 消息参数
     * @return 国际化后的消息
     */
    public static String getMessage(String code, Object... args) {
        String message;
        try {
            message = messageSource.getMessage(code, args, Locale.getDefault());
        } catch (NoSuchMessageException ex) {
            log.warn("message key " + code + " not found", ex);
            return code;
        }
        if (message.isEmpty()) {
            return code;
        }
        return message;
    }

    /**
     * 获取错误码的国际化消息
     *
     * @param errorKey 错误码
     * @param args     消息参数
     * @return 国际化后的消息
     */
    public static String getResponseMessage(String errorKey, Object... args) {
        String code = MESSAGE_KEY_ERROR_MESSAGES + "." + errorKey;
        return getMessage(code, args);
    }
}
