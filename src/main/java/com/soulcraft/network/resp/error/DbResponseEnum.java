package com.soulcraft.network.resp.error;

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

    DB_OPERATION_ERROR(600, "Database operation failed"),
    DUPLICATED_KEY_ERROR(601, "Duplicated key found"),
    RECORD_ALREADY_EXISTED(602, "Record already existed: {0}"),
    RECORD_IN_USE(603, "Record in use."),
    ;

    /**
     * 返回码
     */
    private int code;
    /**
     * 返回消息
     */
    private String message;

    public String getAppName() {
        return "COM";
    }

    public String getModuleName() {
        return "DB";
    }
}
