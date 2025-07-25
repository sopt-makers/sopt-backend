package org.sopt.app.common.exception;

import org.sopt.app.common.response.ErrorCode;

public class ClientException extends BaseException {

    public ClientException(ErrorCode errorCode) {
        super(errorCode);
    }

}

