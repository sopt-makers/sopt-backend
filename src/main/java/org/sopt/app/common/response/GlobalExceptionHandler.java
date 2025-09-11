package org.sopt.app.common.response;

import io.lettuce.core.RedisConnectionException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

import org.sopt.app.common.exception.UnauthorizedException;
import org.sopt.app.common.response.FailureResponse.FieldError;
import org.springframework.http.*;
import org.springframework.validation.*;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @SlackLogger
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<FailureResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        final BindingResult bindingResult = e.getBindingResult();
        final List<FieldError> errors = FailureResponse.FieldError.of(bindingResult);
        FailureResponse response = new FailureResponse(ErrorCode.INVALID_PARAMETER, errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(BindException.class)
    @SlackLogger
    protected ResponseEntity<FailureResponse> handleBindException(final BindException e) {
        log.error(">>> handle: BindException ", e);
        final FailureResponse response = FailureResponse.of(ErrorCode.INVALID_PARAMETER,e.getBindingResult());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @SlackLogger
    public ResponseEntity<FailureResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        final String value = e.getValue() == null ? "" : e.getValue().toString();
        final List<FieldError> errors = FailureResponse.FieldError.of(e.getName(), value, e.getErrorCode());
        return new ResponseEntity<>(new FailureResponse(ErrorCode.INVALID_PARAMETER, errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @SlackLogger
    protected ResponseEntity<FailureResponse> handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
        log.error(">>> handle: HttpRequestMethodNotSupportedException ", e);
        final FailureResponse response = FailureResponse.of(ErrorCode.INVALID_METHOD);
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

//    @ExceptionHandler(Exception.class)
    @SlackLogger
    protected ResponseEntity<FailureResponse> handleException(final Exception e) {
        log.error(">>> handle: Exception ", e);
        String errorMessage = e.getMessage() != null ? e.getMessage() : "Internal Server Error";
        List<FailureResponse.FieldError> errors = FailureResponse.FieldError.of("Exception", "", errorMessage);
        final FailureResponse response = FailureResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, errors);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RedisConnectionException.class)
    @SlackLogger
    protected ResponseEntity<FailureResponse> handleRedisConnectionException(final RedisConnectionException e) {
        log.error(">>> handle: RedisConnectionException ", e);
        String errorMessage = "Redis connection error: " + e.getMessage();
        List<FailureResponse.FieldError> errors = FailureResponse.FieldError.of("RedisConnection", "", errorMessage);
        final FailureResponse response = FailureResponse.of(ErrorCode.REDIS_CONNECTION_ERROR, errors);
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(UnauthorizedException.class)
    protected ResponseEntity<FailureResponse> handleUnauthorizedException(final UnauthorizedException e) {
        final ErrorCode code = e.getErrorCode();                  // 전달된 코드 사용
        final HttpStatus status = code.getHttpStatus();           // 코드가 가진 status 사용 (401/403 등)
        return new ResponseEntity<>(FailureResponse.of(code), status);
    }
}
