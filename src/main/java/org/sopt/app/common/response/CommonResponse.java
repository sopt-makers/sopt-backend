package org.sopt.app.common.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {

    HttpStatus statusCode;
    String responseMessage;

    public static CommonResponse onFailure(HttpStatus statusCode, String responseMessage) {
        return new CommonResponse<>(statusCode, responseMessage);
    }
}