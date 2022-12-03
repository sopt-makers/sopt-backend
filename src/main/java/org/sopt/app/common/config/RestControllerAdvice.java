package org.sopt.app.common.config;

import org.sopt.app.common.constants.ErrorMessage;
import org.sopt.app.common.exception.ExistUserException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {

    @ExceptionHandler(ExistUserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handler(ExistUserException e) {
        return ErrorMessage.of(e.getMessage());
//        return e.getMessage();
    }
}
