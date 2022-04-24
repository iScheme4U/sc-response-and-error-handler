package com.soulcraft.network.resp;

import com.soulcraft.network.resp.error.HttpStatusEnum;
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
public class Response<T> extends BaseResponse {
    private T data;

    protected Response(IResponseEnum responseEnum, T data, Object... args) {
        this(responseEnum.getAppName(),
                responseEnum.getModuleName(),
                responseEnum.getCode(),
                MessageUtils.getResponseMessage(responseEnum.toString(), args),
                data);
    }

    protected Response(String appName, String moduleName, int code, String message, T data) {
        super(appName, moduleName, code, message);
        this.data = data;
    }

    /**
     * 成功返回结果
     */
    public static <T> Response<T> success() {
        return new Response<>(HttpStatusEnum.OK, null);
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> Response<T> success(T data) {
        return new Response<>(HttpStatusEnum.OK, data);
    }

    /**
     * 成功返回结果
     *
     * @param data    获取的数据
     * @param message 自定义消息
     * @param <T>     返回类型
     * @return 成功的返回结果
     */
    public static <T> Response<T> success(T data, String message) {
        HttpStatusEnum status = HttpStatusEnum.OK;
        return new Response<>(status.getAppName(), status.getModuleName(), status.getCode(), message, data);
    }

    /**
     * 成功返回结果
     *
     * @param data        获取的数据
     * @param messageCode 自定义消息代码
     * @param args        自定义消息参数列表
     * @param <T>         返回类型
     * @return 成功的返回结果
     */
    public static <T> Response<T> success(T data, String messageCode, Object... args) {
        String message = MessageUtils.getMessage(messageCode, args);
        return Response.success(data, message);
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     */
    public static <T> Response<T> failed(IResponseEnum errorCode) {
        return new Response<>(errorCode, null);
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     * @param data      数据对象
     * @param args      参数列表
     */
    public static <T> Response<T> failed(IResponseEnum errorCode, T data, Object... args) {
        return new Response<>(errorCode, data, args);
    }

    /**
     * 未登录返回结果
     *
     * @param data 数据对象
     * @param args 参数列表
     */
    public static <T> Response<T> unauthorized(T data, Object... args) {
        return failed(HttpStatusEnum.UNAUTHORIZED, data, args);
    }

    /**
     * 未授权返回结果
     * @param data      数据对象
     * @param args      参数列表
     */
    public static <T> Response<T> forbidden(T data, Object... args) {
        return failed(HttpStatusEnum.FORBIDDEN, data, args);
    }
}
