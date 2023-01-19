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

	protected R(IResponseEnum responseEnum, T data, Object... args) {
		this(responseEnum.getAppName(),
				responseEnum.getModuleName(),
				responseEnum.getCode(),
				MessageUtils.getResponseMessage(responseEnum, args),
				data);
	}

	protected R(String appName, String moduleName, int code, String message, T data) {
		super(appName, moduleName, code, message);
		this.data = data;
	}

	/**
	 * 成功返回结果
	 */
	public static <T> R<T> success() {
		return new R<>(HttpStatusEnum.OK, null);
	}

	/**
	 * 成功返回结果
	 *
	 * @param data 获取的数据
	 */
	public static <T> R<T> success(T data) {
		return new R<>(HttpStatusEnum.OK, data);
	}

	/**
	 * 成功返回结果
	 *
	 * @param data    获取的数据
	 * @param message 自定义消息
	 * @param <T>     返回类型
	 * @return 成功的返回结果
	 */
	public static <T> R<T> success(T data, String message) {
		HttpStatusEnum status = HttpStatusEnum.OK;
		return new R<>(status.getAppName(), status.getModuleName(), status.getCode(), message, data);
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
		String message = MessageUtils.getResponseMessage(messageCode, args);
		return R.success(data, message);
	}

	/**
	 * 失败返回结果
	 *
	 * @param data      数据对象
	 * @param errorCode 错误码
	 * @param args      参数列表
	 */
	public static <T> R<T> failed(T data, IResponseEnum errorCode, Object... args) {
		return new R<>(errorCode, data, args);
	}

	/**
	 * 失败返回结果
	 *
	 * @param errorCode 错误码
	 * @param args      参数列表
	 */
	public static <T> R<T> failed(IResponseEnum errorCode, Object... args) {
		return new R<>(errorCode, null, args);
	}

}
