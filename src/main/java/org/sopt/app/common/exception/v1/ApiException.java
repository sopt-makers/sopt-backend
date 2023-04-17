package org.sopt.app.common.exception.v1;

import lombok.Getter;
import org.sopt.app.common.ResponseCode;
import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    @Getter
    private final String resultCode;

//    @Getter private final String resultMessage;

    @Getter
    private final HttpStatus httpStatus;

//    @Getter
//    private final transient AppObject<Object> body;

//    public ApiException(String resultCode, String resultMessage) {
//        this(resultCode, resultMessage, null);
//    }

//    public ApiException(ResponseCode responseCode, String resultMessage) {
//        super("[" + responseCode.getResponseCode() + "] " + resultMessage);
//        this.resultCode = responseCode.getResponseCode();
//        //this.resultMessage = resultMessage;
//        //this.body = null;
//        this.httpStatus = responseCode.getHttpStatus();
//    }

//    public ApiException(String resultCode, String resultMessage, AppObject<Object> body) {
//        super("[" + resultCode + "] " + resultMessage);
//        if (StringUtils.isEmpty(resultCode)) {
//            this.resultCode = SERVER_ERROR.getResponseCode();
//            this.resultMessage = SERVER_ERROR.getMessage();
//            this.body = null;
//            this.httpStatus = SERVER_ERROR.getHttpStatus();
//        } else {
//            this.resultCode = resultCode;
//            this.resultMessage = resultMessage;
//            this.body = body;
//            this.httpStatus = ResponseCode.getHttpStatusFromResponseCode(resultCode);
//        }
//    }

//    public ApiException() {
//        this(SERVER_ERROR);
//    }

    public ApiException(ResponseCode responseCode) {
        super("[" + responseCode.getResponseCode() + "] " + responseCode.getMessage());
        this.resultCode = responseCode.getResponseCode();
//        this.resultMessage = responseCode.getMessage();
        this.httpStatus = responseCode.getHttpStatus();
//        this.body = null;
    }

//    public ApiException(ResponseCode responseCode, AppObject<Object> body) {
//        super("[" + responseCode.getResponseCode() + "] " + responseCode.getMessage());
//        this.resultCode = responseCode.getResponseCode();
//        //this.resultMessage = responseCode.getMessage();
//        this.httpStatus = responseCode.getHttpStatus();
//        //this.body = body;
//    }

}
