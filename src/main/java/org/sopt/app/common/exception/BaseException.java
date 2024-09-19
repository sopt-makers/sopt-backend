package org.sopt.app.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {

    HttpStatus statusCode;
    String responseMessage;

    public BaseException() {
    }

    public BaseException(HttpStatus statusCode) {
        super();
        this.statusCode = statusCode;
    }

    public BaseException(HttpStatus statusCode, String responseMessage) {
        super(responseMessage);
        this.statusCode = statusCode;
        this.responseMessage = responseMessage;
    }
}
