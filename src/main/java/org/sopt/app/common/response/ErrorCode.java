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
    INVALID_PLAYGROUND_TOKEN("유효하지 않은 플레이그라운드 토큰입니다."),

    // PLAYGROUND AUTH
    PLAYGROUND_PROFILE_NOT_EXISTS("플레이그라운드 프로필을 등록하지 않은 유저입니다."),

    // USER
    USER_NOT_FOUND("존재하지 않는 유저입니다."),
    DUPLICATE_NICKNAME("사용 중인 닉네임입니다."),

    // MISSION
    MISSION_NOT_FOUND("존재하지 않는 미션입니다."),

    // STAMP
    STAMP_NOT_FOUND("존재하지 않는 스탬프입니다."),
    DUPLICATE_STAMP("이미 해당 미션에 대한 스탬프가 존재합니다."),

    // S3
    PRE_SIGNED_URI_ERROR("URL을 생성할 수 없습니다.");

    private final String message;
}
