package com.soulcraft.network.resp.error;

import com.soulcraft.network.exception.BusinessExceptionAssert;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 * 文件返回信息枚举
 * </p>
 *
 * @author Scott
 * @since 2022-03-10
 */
@Getter
@AllArgsConstructor
public enum FileResponseEnum implements BusinessExceptionAssert {

    FILENAME_IS_EMPTY(900, "File name is empty."),
    NOT_SUPPORTED_FILE_EXTENSION(901, "Not supported file extension."),
    FILE_SIZE_EXCEEDS_THRESHOLD(902, "File {0} size exceeds threshold {1}"),
    FILENAME_CONTAINS_ILLEGAL_CHARACTER(903, "Filename {0} contains illegal character."),
    CANNOT_CREATE_DIRECTORY(904, "Cannot create directory {0}."),
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
        return "FILE";
    }
}
