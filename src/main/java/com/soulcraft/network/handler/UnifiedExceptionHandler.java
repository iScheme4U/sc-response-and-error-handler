package com.soulcraft.network.handler;

import com.soulcraft.network.exception.BaseException;
import com.soulcraft.network.exception.BusinessException;
import com.soulcraft.network.resp.error.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLException;

/**
 * <p>
 * 全局错误处理器
 * </p>
 *
 * @author Scott
 * @since 2022-03-10
 */
@Slf4j
@ControllerAdvice
public class UnifiedExceptionHandler {

	/**
	 * 业务异常
	 *
	 * @param e 异常
	 * @return 异常结果
	 */
	@ExceptionHandler(value = BusinessException.class)
	@ResponseBody
	public ErrorResponse handleBusinessException(BusinessException e) {
		log.error(e.getMessage(), e);
		// 此处 e.getLocalizedMessage() 已是国际化后的消息
		return new ErrorResponse(e.getResponseEnum(), e.getLocalizedMessage());
	}

	/**
	 * 自定义异常
	 *
	 * @param e 异常
	 * @return 异常结果
	 */
	@ExceptionHandler(value = BaseException.class)
	@ResponseBody
	public ErrorResponse handleBaseException(BaseException e) {
		log.error(e.getMessage(), e);
		return new ErrorResponse(e.getResponseEnum(), e.getLocalizedMessage());
	}

	/**
	 * Controller上一层相关异常
	 *
	 * @param e 异常
	 * @return 异常结果
	 */
	@ExceptionHandler({
			NoHandlerFoundException.class,
			HttpRequestMethodNotSupportedException.class,
			HttpMediaTypeNotSupportedException.class,
			MissingPathVariableException.class,
			MissingServletRequestParameterException.class,
			TypeMismatchException.class,
			HttpMessageNotReadableException.class,
			HttpMessageNotWritableException.class,
			HttpMediaTypeNotAcceptableException.class,
			ServletRequestBindingException.class,
			ConversionNotSupportedException.class,
			MissingServletRequestPartException.class,
			AsyncRequestTimeoutException.class
	})
	@ResponseBody
	public ErrorResponse handleServletException(Exception e) {
		log.error(e.getMessage(), e);
		try {
			ServletResponseEnum servletExceptionEnum = ServletResponseEnum.valueOf(e.getClass().getSimpleName());
			return new ErrorResponse(servletExceptionEnum, e.getLocalizedMessage());
		} catch (IllegalArgumentException e1) {
			log.error("class [{}] not defined in enum {}", e.getClass().getName(), ServletResponseEnum.class.getName());
		}

		return new ErrorResponse(HttpStatusEnum.INTERNAL_SERVER_ERROR);
	}


	/**
	 * 参数绑定异常
	 *
	 * @param e 异常
	 * @return 异常结果
	 */
	@ExceptionHandler(value = BindException.class)
	@ResponseBody
	public ErrorResponse handleBindException(BindException e) {
		log.error(e.getMessage(), e);
		return wrapperBindingResult(e.getBindingResult());
	}

	/**
	 * 方法参数异常，将校验失败的所有异常组合成一条错误信息
	 *
	 * @param e 异常
	 * @return 异常结果
	 */
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	@ResponseBody
	public ErrorResponse handleValidException(MethodArgumentNotValidException e) {
		log.error(e.getMessage(), e);
		return wrapperBindingResult(e.getBindingResult());
	}

	/**
	 * 包装绑定异常结果
	 *
	 * @param bindingResult 绑定结果
	 * @return 异常结果
	 */
	private ErrorResponse wrapperBindingResult(BindingResult bindingResult) {
		StringBuilder msg = new StringBuilder();

		boolean first = true;
		for (ObjectError error : bindingResult.getAllErrors()) {
			if (first) {
				first = false;
			} else {
				msg.append(", ");
			}
			if (error instanceof FieldError) {
				msg.append(((FieldError) error).getField()).append(": ");
			}
			msg.append(error.getDefaultMessage() == null ? "" : error.getDefaultMessage());
		}

		return new ErrorResponse(CommonResponseEnum.VALIDATE_FAILED, msg.toString());
	}

	@ResponseBody
	@ExceptionHandler(value = DuplicateKeyException.class)
	public ErrorResponse handleException(DuplicateKeyException e) {
		log.error(e.getMessage(), e);
		return new ErrorResponse(DbResponseEnum.DUPLICATED_KEY_ERROR);
	}

	@ResponseBody
	@ExceptionHandler(value = DataAccessException.class)
	public ErrorResponse handleException(DataAccessException e) {
		log.error(e.getMessage(), e);
		return new ErrorResponse(DbResponseEnum.DB_OPERATION_ERROR);
	}

	@ResponseBody
	@ExceptionHandler(value = SQLException.class)
	public ErrorResponse handleException(SQLException e) {
		log.error(e.getMessage(), e);
		return new ErrorResponse(DbResponseEnum.DB_OPERATION_ERROR);
	}

	/**
	 * 未定义异常
	 *
	 * @param e 异常
	 * @return 异常结果
	 */
	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public ErrorResponse handleException(Exception e) {
		log.error(e.getMessage(), e);
		return new ErrorResponse(HttpStatusEnum.INTERNAL_SERVER_ERROR);
	}
}
