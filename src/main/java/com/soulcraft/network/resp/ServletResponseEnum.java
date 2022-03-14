package com.soulcraft.network.resp;

import com.soulcraft.network.exception.BusinessExceptionAssert;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 * Servlet返回信息枚举
 * </p>
 *
 * @author Scott
 * @since 2022-03-10
 */
@Getter
@AllArgsConstructor
public enum ServletResponseEnum implements BusinessExceptionAssert {
    ;
    /**
     * 返回码
     */
    private long code;
    /**
     * 返回消息
     */
    private String message;
}
