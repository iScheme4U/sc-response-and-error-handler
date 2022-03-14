package com.soulcraft.network.exception;

import com.soulcraft.network.resp.IResponseEnum;
import com.soulcraft.network.util.MessageUtils;

/**
 * <p>
 * 业务错误信息断言
 * </p>
 *
 * @author Scott
 * @since 2022-03-10
 */
public interface BusinessExceptionAssert extends IResponseEnum, Assert {

    @Override
    default BaseException newException(Object... args) {
        String msg = MessageUtils.getResponseMessage(this.toString(), args);
        return new BusinessException(this, args, msg);
    }

    @Override
    default BaseException newException(Throwable cause, Object... args) {
        String msg = MessageUtils.getResponseMessage(this.toString(), args);
        return new BusinessException(this, args, msg, cause);
    }

}
