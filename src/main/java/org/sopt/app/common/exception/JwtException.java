package org.sopt.app.common.exception;

import org.sopt.app.common.response.ErrorCode;

public class JwtException extends BaseException {
    public JwtException(ErrorCode errorCode) {
        super(errorCode);
    }
}
