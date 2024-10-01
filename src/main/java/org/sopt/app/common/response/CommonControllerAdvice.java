package org.sopt.app.common.response;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.sopt.app.common.exception.BaseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class CommonControllerAdvice {

    @ExceptionHandler(value = BaseException.class)
    @SlackLogger
    public ResponseEntity<FailureResponse> onKnownException(BaseException baseException, HttpServletRequest request) {
        final ErrorCode errorCode = baseException.getErrorCode();
        final FailureResponse response = FailureResponse.of(errorCode);
        return new ResponseEntity<>(response,errorCode.getHttpStatus());
    }
}