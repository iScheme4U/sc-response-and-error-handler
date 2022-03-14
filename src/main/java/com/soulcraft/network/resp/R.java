package com.soulcraft.network.resp;

import com.soulcraft.network.util.MessageUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 通用返回对象
 * </p>
 *
 * @author Scott
 * @since 2022-03-10
 */
@Getter
@Setter
public class R<T> extends BaseResponse {
    private T data;

    protected R(IResponseEnum responseEnum, T data, Object... args) {
        this(responseEnum.getAppName(),
                responseEnum.getModuleName(),
                responseEnum.getCode(),
                MessageUtils.getResponseMessage(responseEnum.toString(), args),
                data);
    }

    protected R(String appName, String moduleName, int code, String message, T data) {
        super(appName, moduleName, code, message);
        this.data = data;
    }

    /**
     * 成功返回结果
     */
    public static <T> R<T> success() {
        return new R<>(CommonResponseEnum.SUCCESS, null);
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> R<T> success(T data) {
        return new R<>(CommonResponseEnum.SUCCESS, data);
    }

    /**
     * 失败返回结果
     */
    public static <T> R<T> failed() {
        return failed(null);
    }

    /**
     * 失败返回结果
     */
    public static <T> R<T> failed(T data, Object... args) {
        return failed(CommonResponseEnum.SERVER_ERROR, data, args);
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     */
    public static <T> R<T> failed(IResponseEnum errorCode, T data, Object... args) {
        return new R<>(errorCode, data, args);
    }

    /**
     * 未登录返回结果
     */
    public static <T> R<T> unauthorized(T data, Object... args) {
        return failed(CommonResponseEnum.UNAUTHORIZED, data, args);
    }

    /**
     * 未授权返回结果
     */
    public static <T> R<T> forbidden(T data, Object... args) {
        return failed(CommonResponseEnum.FORBIDDEN, data, args);
    }
}
