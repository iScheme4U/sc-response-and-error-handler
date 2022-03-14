package com.soulcraft.network.resp;

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

    FILENAME_IS_EMPTY(2000, "File name is empty."),
    NOT_SUPPORTED_FILE_EXTENSION(2001, "Not supported file extension."),
    FILE_SIZE_EXCEEDS_THRESHOLD(2002, "File {0} size exceeds threshold {1}"),
    FILENAME_CONTAINS_ILLEGAL_CHARACTER(2003, "Filename {0} contains illegal character."),
    CANNOT_CREATE_DIRECTORY(2004, "Cannot create directory {0}."),
    ;

    /**
     * 返回码
     */
    private long code;
    /**
     * 返回消息
     */
    private String message;
}
