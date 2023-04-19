package org.sopt.app.common.response;

import org.sopt.app.common.exception.BaseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonControllerAdvice {

    @ExceptionHandler(value = BaseException.class)
    public ResponseEntity onKnownException(BaseException baseException) {
        return new ResponseEntity<>(CommonResponse.onFailure(baseException.getStatusCode(),
                baseException.getResponseMessage()), null, baseException.getStatusCode());
    }

}