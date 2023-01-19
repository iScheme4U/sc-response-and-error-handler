package com.soulcraft.network.exception;

import com.soulcraft.network.resp.IResponseEnum;
import lombok.Getter;

/**
 * <p>
 * 基础异常
 * </p>
 *
 * @author Scott
 * @since 2022-03-10
 */
@Getter
public class BaseException extends RuntimeException {
	private final IResponseEnum responseEnum;
	private final Object[] args;

	/**
	 * 构造基础异常对象
	 *
	 * @param responseEnum 返回信息枚举
	 */
	public BaseException(IResponseEnum responseEnum) {
		this(responseEnum, null, responseEnum.getMessage());
	}

	/**
	 * 构造基础异常对象
	 *
	 * @param responseEnum 返回信息枚举
	 * @param args         参数
	 * @param message      其他信息
	 */
	public BaseException(IResponseEnum responseEnum, Object[] args, String message) {
		this(responseEnum, args, message, null);
	}

	/**
	 * 构造基础异常对象
	 *
	 * @param responseEnum 返回信息枚举
	 * @param args         参数
	 * @param message      其他信息
	 */
	public BaseException(IResponseEnum responseEnum, Object[] args, String message, Throwable cause) {
		super(message, cause);
		this.responseEnum = responseEnum;
		this.args = args;
	}
}
