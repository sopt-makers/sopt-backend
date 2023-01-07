package org.sopt.app.common.exception;

import static org.sopt.app.common.constants.Constants.RESULT_CODE;
import static org.sopt.app.common.constants.Constants.RESULT_MESSAGE;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler({ApiException.class})
  protected void handle(ApiException apiException, HttpServletRequest request,
      HttpServletResponse response) {

    final String encodingResultMessage = URLEncoder
        .encode(apiException.getMessage(), StandardCharsets.UTF_8);

    response.setHeader(RESULT_CODE, apiException.getResultCode());
    response.setHeader(RESULT_MESSAGE, encodingResultMessage);
    response.setStatus(apiException.getHttpStatus().value());
  }

}
