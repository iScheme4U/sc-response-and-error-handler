package com.soulcraft.network.resp;

import com.soulcraft.network.exception.BusinessExceptionAssert;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 * 通用返回信息枚举
 * </p>
 *
 * @author Scott
 * @since 2022-03-10
 */
@Getter
@AllArgsConstructor
public enum CommonResponseEnum implements BusinessExceptionAssert {

    SUCCESS(200, "Success"),
    UNAUTHORIZED(401, "Not logged in or token expired."),
    INVALID_PARAMETER(402, "Invalid parameter."),
    FORBIDDEN(403, "Forbidden"),
    SERVER_ERROR(500, "Server Error, cause: {0}"),
    VALIDATE_FAILED(501, "Validate failed"),
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
        return "SRV";
    }

}
