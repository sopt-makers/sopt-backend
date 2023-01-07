package org.sopt.app.common;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum ResponseCode {

    INVALID_RESPONSE("99", "99", "9999", "요청이 처리 되지 않았습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    SUCCESS("00","00", "0000", "정상 처리되었습니다.", HttpStatus.OK),
    INVALID_REQUEST("00", "01", "0001", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_STAMP("00", "02", "0002", "중복된 스탬프 등록 요청입니다.", HttpStatus.BAD_REQUEST);

    private final String codeGroup;
    private final String code;
    private final String responseCode;
    private final String message;
    private final HttpStatus httpStatus;


    private static final ImmutableMap<String, ResponseCode> codes = ImmutableMap.copyOf(
            Stream.of(values()).collect(Collectors.toMap(ResponseCode::getResponseCode, Function.identity())));


    ResponseCode(String codeGroup, String code, String responseCode, String message, HttpStatus httpStatus) {
        this.codeGroup = codeGroup;
        this.code = code;
        this.responseCode = responseCode;
        this.message = message;
        this.httpStatus = httpStatus;
    }


    public static ResponseCode getResponseCode(String responseCode){
        if(codes.get(responseCode)!=null)
            return codes.get(responseCode);
        else
            return INVALID_RESPONSE;
    }
//    public String getUrlEncodingMessage(){
//        return URLEncoder.encode(this.message, StandardCharsets.UTF_8);
//    }
}
