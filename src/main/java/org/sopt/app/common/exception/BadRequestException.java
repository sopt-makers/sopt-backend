package org.sopt.app.common.exception;

import org.sopt.app.common.response.ErrorCode;

public class BadRequestException extends BaseException {

    public BadRequestException() {
        super(ErrorCode.BAD_REQUEST);
    }

    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
