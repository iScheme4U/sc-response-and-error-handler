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
     * 断言对象为空，否则抛出异常
     *
     * @param obj  检查的对象
     * @param args 异常消息参数列表
     */
    default void assertNull(Object obj, Object... args) {
        if (obj != null) {
            throwNewException(args);
        }
    }

    /**
     * 断言对象非空，否则抛出异常
     *
     * @param obj  检查的对象
     * @param args 异常消息参数列表
     */
    default void assertNotNull(Object obj, Object... args) {
        if (obj == null) {
            throwNewException(args);
        }
    }

    /**
     * 断言条件为真，否则抛出异常
     *
     * @param condition 检查条件
     * @param args      异常消息参数列表
     */
    default void assertTrue(boolean condition, Object... args) {
        if (!condition) {
            throwNewException(args);
        }
    }

    /**
     * 断言条件为假，否则抛出异常
     *
     * @param condition 检查条件
     * @param args      异常消息参数列表
     */
    default void assertFalse(boolean condition, Object... args) {
        if (condition) {
            throwNewException(args);
        }
    }
}
