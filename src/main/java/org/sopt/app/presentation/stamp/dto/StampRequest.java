package org.sopt.app.presentation.stamp.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class StampRequest {

    @Getter
    @Setter
    @ToString
    public static class RegisterStampRequest {

        private String contents; // 스탬프 내용
    }
}
