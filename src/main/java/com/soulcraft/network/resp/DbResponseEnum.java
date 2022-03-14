package com.soulcraft.network.resp;

import com.soulcraft.network.exception.BusinessExceptionAssert;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 * 数据库返回信息枚举
 * </p>
 *
 * @author Scott
 * @since 2022-03-10
 */
@Getter
@AllArgsConstructor
public enum DbResponseEnum implements BusinessExceptionAssert {

    DUPLICATED_KEY_ERROR(600, "Duplicated key found"),
    RECORD_ALREADY_EXISTED(601, "Record already existed: {0}"),
    RECORD_IN_USE(602, "Record in use."),
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
