package org.sopt.app.common.exception.v1;

public class ExistUserException extends RuntimeException {

    public ExistUserException(String message) {
        super(message);
    }
}