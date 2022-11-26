package org.sopt.app.common.exception;

public class ExistUserException extends RuntimeException {
    public ExistUserException(String message) {
        super(message);
    }
}