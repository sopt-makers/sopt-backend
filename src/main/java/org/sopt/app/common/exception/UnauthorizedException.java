package org.sopt.app.common.exception;

import lombok.Getter;
import org.sopt.app.common.ResponseCode;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends RuntimeException {


    @Getter
    private final String resultCode;
    @Getter
    private final HttpStatus httpStatus;

    public UnauthorizedException(ResponseCode responseCode) {
        super("[" + responseCode.getResponseCode() + "] " + responseCode.getMessage());
        this.resultCode = responseCode.getResponseCode();
        this.httpStatus = responseCode.getHttpStatus();
    }
}
