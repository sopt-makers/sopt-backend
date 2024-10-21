package org.sopt.app.common.exception;

import org.sopt.app.common.response.ErrorCode;

public class NotFoundException extends BaseException {
    public NotFoundException() {
        super(ErrorCode.ENTITY_NOT_FOUND);
    }

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
