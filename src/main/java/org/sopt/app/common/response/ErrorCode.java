package org.sopt.app.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // COMMON
    INVALID_PARAMETER("잘못된 파라미터 입니다."),

    // AUTH
    INVALID_ACCESS_TOKEN("유효하지 않은 앱 어세스 토큰입니다."),
    INVALID_REFRESH_TOKEN("유효하지 않은 앱 리프레시 토큰입니다."),
    INVALID_PLAYGROUND_TOKEN("유효하지 않은 플레이그라운드 토큰입니다."),
    INVALID_PLAYGROUND_CODE("유효하지 않은 플레이그라운드 OAuth 코드입니다."),

    // PLAYGROUND
    PLAYGROUND_USER_NOT_EXISTS("플레이그라운드 유저 정보를 가져올 수 없습니다."),
    PLAYGROUND_PROFILE_NOT_EXISTS("플레이그라운드 프로필을 등록하지 않은 유저입니다."),

    // OPERATION
    OPERATION_PROFILE_NOT_EXISTS("운영 서비스에 존재하지 않는 회원입니다."),

    // USER
    USER_NOT_FOUND("존재하지 않는 유저입니다."),
    DUPLICATE_NICKNAME("사용 중인 닉네임입니다."),

    // MISSION
    MISSION_NOT_FOUND("존재하지 않는 미션입니다."),

    // STAMP
    STAMP_NOT_FOUND("존재하지 않는 스탬프입니다."),
    DUPLICATE_STAMP("이미 해당 미션에 대한 스탬프가 존재합니다."),
    INVALID_STAMP_ACTIVITY_DATE("스탬프 활동 날짜가 존재하지 않습니다."),
    INVALID_STAMP_CONTENTS("스탬프 내용이 존재하지 않습니다."),
    INVALID_STAMP_IMAGES("스탬프 이미지가 존재하지 않습니다."),
    INVALID_STAMP_MISSION_ID("스탬프 미션 ID가 존재하지 않습니다."),
    INVALID_STAMP_ID("스탬프 ID가 존재하지 않습니다."),
    INVALID_STAMP_CREATED_AT("스탬프 생성 날짜가 존재하지 않습니다."),
    INVALID_STAMP_UPDATED_AT("스탬프 업데이트 날짜가 존재하지 않습니다."),

    // NOTIFICATION
    NOTIFICATION_NOT_FOUND("존재하지 않는 알림입니다."),

    // NOTIFICATION_OPTION
    TARGET_OPTION_NOT_FOUND("해당 알림 동의 여부 값이 존재하지 않습니다."),

    // PUSH_TOKEN
    PUSH_TOKEN_NOT_FOUND_FROM_LOCAL("서비스 내에 존재하지 않는 푸시 토큰입니다."),
    PUSH_TOKEN_NOT_FOUND_FROM_EXTERNAL("외부 푸시 서버 내에 존재하지 않는 푸시 토큰입니다."),

    INVALID_REQUEST_BODY_FOR_PUSH_TOKEN("잘못된 푸시 토큰 관련 요청입니다."),
    INTERNAL_SERVER_ERROR_IN_PUSH_SERVER("푸시 서버 내부 오류입니다."),

    // POKE
    POKE_HISTORY_NOT_FOUND("해당 찌르기 내역은 존재하지 않습니다."),
    POKE_MESSAGE_NOT_FOUND("해당 찌르기 메시지는 존재하지 않습니다."),
    POKE_MESSAGE_TYPE_NOT_FOUND("해당 찌르기 메시지 타입은 존재하지 않습니다."),
    POKE_MESSAGE_MUST_NOT_BE_NULL("찌르기 메시지 타입은 필수 값입니다."),
    OVER_DAILY_POKE_LIMIT("오늘은 콕 찌르기를 다 썼어요."),
    DUPLICATE_POKE("이미 찌르기를 보낸 친구입니다."),

    // FRIEND
    FRIENDSHIP_NOT_FOUND("해당 친구관계는 존재하지 않습니다."),

    // ADMIN
    INVALID_APP_ADMIN_PASSWORD("잘못된 앱 어드민 패스워드입니다."),
    ADMIN_INIT_SOPTAMP_USER_FAILED("솝탬프 유저 초기화에 실패했습니다."),

    // S3
    PRE_SIGNED_URI_ERROR("URL을 생성할 수 없습니다.");

    private final String message;
}
