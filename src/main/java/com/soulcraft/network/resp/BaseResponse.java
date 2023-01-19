package com.soulcraft.network.resp;

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
public abstract class BaseResponse implements IResponse {

	/**
	 * 整个错误码信息
	 */
	private String code;
	/**
	 * 返回消息
	 */
	private String message;

	public BaseResponse(String appName, String moduleName, int code, String message) {
		this.code = BaseResponse.getFullCode(appName, moduleName, code);
		this.message = message;
	}

	public static String getFullCode(String appName, String moduleName, int code) {
		return appName + "-" + moduleName + "-" + code;
	}
}
