package com.soulcraft.network.resp.error;

import com.soulcraft.network.exception.BusinessExceptionAssert;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 * 通用返回信息枚举
 * </p>
 *
 * @author Scott
 * @since 2022-03-10
 */
@Getter
@AllArgsConstructor
public enum CommonResponseEnum implements BusinessExceptionAssert {

	VALIDATE_FAILED(800, "Validate failed"),
	INVALID_PARAMETER(801, "Invalid parameter."),
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
		return "SRV";
	}

}
