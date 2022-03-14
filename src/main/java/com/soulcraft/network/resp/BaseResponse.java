package com.soulcraft.network.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 基础响应信息类
 * </p>
 *
 * @author Scott
 * @since 2022-03-10
 */
@Getter
@Setter
@AllArgsConstructor
public class BaseResponse implements IResponseEnum {
    /**
     * 返回码
     */
    private long code;
    /**
     * 返回消息
     */
    private String message;
}
