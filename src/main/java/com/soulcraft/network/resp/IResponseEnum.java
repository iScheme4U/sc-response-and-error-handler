package com.soulcraft.network.resp;

/**
 * <p>
 * 返回信息枚举
 * </p>
 *
 * @author Scott
 * @since 2022-03-10
 */
public interface IResponseEnum {
    /**
     * 系统/应用 简称
     *
     * @return 系统/应用 简称
     */
    String getAppName();

    /**
     * 模块/组件 简称
     *
     * @return 模块/组件 简称
     */
    String getModuleName();

    /**
     * 返回码
     *
     * @return 返回码
     */
    int getCode();

    /**
     * <pre>
     * 整个错误码信息，包含：
     * 1. 系统/应用 简称
     * 2. 模块/组件 简称
     * 3. 返回码
     * </pre>
     *
     * @return 整个错误码信息
     */
    default String getFullCode() {
        return BaseResponse.getFullCode(getAppName(), getModuleName(), getCode());
    }

    /**
     * 返回消息
     *
     * @return 返回消息
     */
    String getMessage();
}
