package com.soulcraft.network.exception;

import com.soulcraft.network.resp.IResponseEnum;

/**
 * <p>
 * 业务逻辑异常
 * </p>
 *
 * @author Scott
 * @since 2022-03-10
 */
public class BusinessException extends BaseException {
	/**
	 * 构造业务异常对象
	 *
	 * @param responseEnum 返回信息枚举
	 * @param args         参数
	 * @param message      其他信息
	 */
	public BusinessException(IResponseEnum responseEnum, Object[] args, String message) {
		super(responseEnum, args, message);
	}

	/**
	 * @param responseEnum 返回信息枚举
	 * @param args         参数
	 * @param message      其他信息
	 * @param cause        原因
	 */
	public BusinessException(IResponseEnum responseEnum, Object[] args, String message, Throwable cause) {
		super(responseEnum, args, message, cause);
	}
}
