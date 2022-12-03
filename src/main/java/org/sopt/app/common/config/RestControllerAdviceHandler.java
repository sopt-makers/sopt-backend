package org.sopt.app.common.config;

import org.sopt.app.common.constants.ErrorMessage;
import org.sopt.app.common.exception.ExistUserException;
import org.sopt.app.common.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestControllerAdviceHandler {

    @ExceptionHandler(value = {ExistUserException.class, UserNotFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handler(Exception e) {
        return ErrorMessage.of(e.getMessage());
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage bindExceptionHandler(BindException e) {
        return ErrorMessage.of(e.getBindingResult().getFieldError().getDefaultMessage());
    }
}
