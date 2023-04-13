package org.sopt.app.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // COMMON
    INVALID_PARAMETER("잘못된 파라미터 입니다."),

    // TOKEN
    INVALID_APP_TOKEN("유효하지 않은 앱 토큰입니다."),
    INVALID_PLAYGROUND_TOKEN("유효하지 않은 플레이그라운드 토큰입니다.");

    private final String message;
}
