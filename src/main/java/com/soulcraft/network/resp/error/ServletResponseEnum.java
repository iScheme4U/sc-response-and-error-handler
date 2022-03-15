package com.soulcraft.network.resp.error;

import com.soulcraft.network.exception.BusinessExceptionAssert;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 * Servlet返回信息枚举
 * </p>
 *
 * @author Scott
 * @since 2022-03-10
 */
@Getter
@AllArgsConstructor
public enum ServletResponseEnum implements BusinessExceptionAssert {
    NoHandlerFoundException(700, "No handler found exception"),
    HttpRequestMethodNotSupportedException(701, "Http request method not supported exception"),
    HttpMediaTypeNotSupportedException(702, "Http media type not supported exception"),
    MissingPathVariableException(703, "Missing path variable exception"),
    MissingServletRequestParameterException(704, "Missing servlet request parameter exception"),
    TypeMismatchException(705, "Type mismatch exception"),
    HttpMessageNotReadableException(706, "Http message not readable exception"),
    HttpMessageNotWritableException(707, "Http message not writable exception"),
    HttpMediaTypeNotAcceptableException(708, "Http media type not acceptable exception"),
    ServletRequestBindingException(709, "Servlet request binding exception"),
    ConversionNotSupportedException(710, "Conversion not supported exception"),
    MissingServletRequestPartException(711, "Missing servlet request part exception"),
    AsyncRequestTimeoutException(712, "Async request timeout exception");
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
        return "SERVLET";
    }
}
