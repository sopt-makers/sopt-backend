package org.sopt.app.common.exception;

import org.sopt.app.common.response.ErrorCode;

public class ConflictException extends BaseException {

    public ConflictException() {
        super(ErrorCode.CONFLIECT);
    }

    public ConflictException(ErrorCode errorCode) {
        super(errorCode);
    }
}
