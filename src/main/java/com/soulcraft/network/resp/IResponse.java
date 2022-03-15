package com.soulcraft.network.resp;

/**
 * <p>
 * 响应信息接口
 * </p>
 *
 * @author Scott
 * @since 2022-03-15
 */
public interface IResponse {
    /**
     * @return 错误码
     */
    String getCode();

    /**
     * @return 错误信息
     */
    String getMessage();
}
