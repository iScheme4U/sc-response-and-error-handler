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
public class R<T> extends BaseResponse {
	private T data;

	private R(T data, IResponseEnum responseEnum, Object... args) {
		this(data, responseEnum.getAppName(),
				responseEnum.getModuleName(),
				responseEnum.getCode(),
				MessageUtils.getResponseMessage(responseEnum, args)
		);
	}

	private R(T data, String appName, String moduleName, int code, String message) {
		super(appName, moduleName, code, message);
		this.data = data;
	}

	/**
	 * 成功返回结果
	 */
	public static <T> R<T> success() {
		return R.success(null);
	}

	/**
	 * 成功返回结果
	 *
	 * @param data 获取的数据
	 */
	public static <T> R<T> success(T data) {
		return new R<>(data, HttpStatusEnum.OK);
	}

	/**
	 * 成功返回结果
	 *
	 * @param messageCode 自定义消息代码
	 * @param args        自定义消息参数列表
	 * @param <T>         返回类型
	 * @return 成功的返回结果
	 */
	public static <T> R<T> success(IResponseEnum messageCode, Object... args) {
		return R.success(null, messageCode, args);
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
	public static <T> R<T> success(T data, IResponseEnum messageCode, Object... args) {
		HttpStatusEnum status = HttpStatusEnum.OK;
		String message = MessageUtils.getResponseMessage(messageCode, args);
		return new R<>(data, status.getAppName(), status.getModuleName(), status.getCode(), message);
	}

	/**
	 * 失败返回结果
	 *
	 * @param errorCode 错误码
	 * @param args      参数列表
	 */
	public static <T> R<T> failed(IResponseEnum errorCode, Object... args) {
		return R.failed(null, errorCode, args);
	}

	/**
	 * 失败返回结果
	 *
	 * @param data      数据对象
	 * @param errorCode 错误码
	 * @param args      参数列表
	 */
	public static <T> R<T> failed(T data, IResponseEnum errorCode, Object... args) {
		return new R<>(data, errorCode, args);
	}
}
