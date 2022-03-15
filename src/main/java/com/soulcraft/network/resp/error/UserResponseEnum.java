package com.soulcraft.network.resp.error;

import com.soulcraft.network.exception.BusinessExceptionAssert;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 * 用户操作返回信息枚举
 * </p>
 *
 * @author Scott
 * @since 2022-03-10
 */
@Getter
@AllArgsConstructor
public enum UserResponseEnum implements BusinessExceptionAssert {

    USERNAME_OR_PASSWORD_IS_INCORRECT(1000, "Username or password is incorrect."),
    USER_IS_LOCKED(1001, "User is locked"),
    USER_LOGIN_FAILED(1002, "User login failed"),
    TOKEN_EXPIRED(1003, "Token expired"),
    USER_NOT_FOUND(1004, "User not found"),
    PASSWORD_IS_INCORRECT(1005, "Password is incorrect."),
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
        return "USER";
    }
}
