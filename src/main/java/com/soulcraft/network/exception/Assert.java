package com.soulcraft.network.exception;

/**
 * <p>
 * 异常断言
 * </p>
 *
 * @author Scott
 * @since 2022-03-10
 */
public interface Assert {
    /**
     * 创建异常
     *
     * @param args 参数列表
     * @return BaseException 基础异常
     */
    BaseException newException(Object... args);

    /**
     * 抛出异常
     *
     * @param args 参数列表
     */
    default void throwNewException(Object... args) throws BaseException {
        throw newException(args);
    }

    /**
     * 创建异常
     *
     * @param cause 原因
     * @param args  参数列表
     * @return BaseException 基础异常
     */
    BaseException newException(Throwable cause, Object... args);

    /**
     * 抛出异常
     *
     * @param cause 原因
     * @param args  参数列表
     */
    default void throwNewException(Throwable cause, Object... args) throws BaseException {
        throw newException(cause, args);
    }

    /**
     * <p>断言对象<code>obj</code>非空。如果对象<code>obj</code>为空，则抛出异常
     *
     * @param obj 待判断对象
     */
    default void assertNotNull(Object obj) {
        if (obj == null) {
            throwNewException();
        }
    }

    /**
     * <p>断言对象<code>obj</code>非空。如果对象<code>obj</code>为空，则抛出异常
     * <p>异常信息<code>message</code>支持传递参数方式，避免在判断之前进行字符串拼接操作
     *
     * @param obj  待判断对象
     * @param args message占位符对应的参数列表
     */
    default void assertNotNull(Object obj, Object... args) {
        if (obj == null) {
            throwNewException(args);
        }
    }
}
