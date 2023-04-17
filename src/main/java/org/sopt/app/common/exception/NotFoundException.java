package org.sopt.app.common.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseException {

    public NotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

}
