package org.sopt.app.common.exception;

import org.sopt.app.common.response.ErrorCode;

public class JwkException extends BaseException {
    public JwkException(ErrorCode errorCode) {
        super(errorCode);
    }
}
