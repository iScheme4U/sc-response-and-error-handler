package com.soulcraft.network.resp;

/**
 * <p>
 * 错误返回信息
 * </p>
 *
 * @author Scott
 * @since 2022-03-10
 */
public class ErrorResponse extends BaseResponse {
    /**
     * 构造错误返回信息
     *
     * @param code    返回码
     * @param message 返回信息
     */
    public ErrorResponse(String appName, String moduleName, int code, String message) {
        super(appName, moduleName, code, message);
    }

    public ErrorResponse(IResponseEnum responseEnum, String message) {
        this(responseEnum.getAppName(), responseEnum.getModuleName(), responseEnum.getCode(), message);
    }
}
