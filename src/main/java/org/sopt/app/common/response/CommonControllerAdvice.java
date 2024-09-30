package org.sopt.app.common.response;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.sopt.app.application.slack.SlackService;
import org.sopt.app.common.exception.BaseException;
import org.sopt.app.domain.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class CommonControllerAdvice {

    @ExceptionHandler(value = BaseException.class)
    public ResponseEntity<FailureResponse> onKnownException(BaseException baseException) {
        SlackService.sendSlackMessage("Error", baseException.getMessage());
        final ErrorCode errorCode = baseException.getErrorCode();
        final FailureResponse response = FailureResponse.of(errorCode);
        return new ResponseEntity<>(response,errorCode.getHttpStatus());
    }

    @NotNull
    private String getMessage(
            BaseException baseException, Long userId, String requestMethod, String requestUri,
            String baseExceptionMessage,
            HttpStatus baseExceptionStatusCode
    ) {
        return "유저 아이디: " + userId + "\n" +
                "요청 URI: [" + requestMethod + "] " + requestUri + "\n" +
                "오류 메시지: " + baseExceptionMessage + "\n" +
                "오류 코드: " + baseExceptionStatusCode + "\n" +
                "StackTrace" + Arrays.toString(baseException.getStackTrace());
    }

    private Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        val user = (User) authentication.getPrincipal();
        return user.getId();
    }

}