package org.sopt.app.common.constants;

import lombok.Getter;

@Getter
public class ErrorMessage {
    private String message;

    public ErrorMessage(String message) {
        this.message = message;
    }

    public static ErrorMessage of(String message){
        return new ErrorMessage(message);
    }
}
