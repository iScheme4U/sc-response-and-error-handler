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
     * 返回码
     *
     * @return 返回码
     */
    long getCode();

    /**
     * 返回消息
     *
     * @return 返回消息
     */
    String getMessage();
}
