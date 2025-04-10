package org.sopt.app.common.response;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // COMMON
    INVALID_PARAMETER("잘못된 파라미터 입니다.", HttpStatus.BAD_REQUEST),
    INVALID_METHOD("잘못된 METHOD 요청입니다.", HttpStatus.METHOD_NOT_ALLOWED),
    INTERNAL_SERVER_ERROR("서버 내부 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    REDIS_CONNECTION_ERROR("레디스 연결에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ENTITY_NOT_FOUND("객체를 찾을수 없습니다.", HttpStatus.NOT_FOUND),
    BAD_REQUEST("잘못된 요청입니다", HttpStatus.BAD_REQUEST),
    FORBIDDEN("접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    // AUTH
    INVALID_ACCESS_TOKEN("유효하지 않은 앱 어세스 토큰입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("유효하지 않은 앱 리프레시 토큰입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_PLAYGROUND_TOKEN("유효하지 않은 플레이그라운드 토큰입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_PLAYGROUND_CODE("유효하지 않은 플레이그라운드 OAuth 코드입니다.", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED("권한이 없습니다", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    // PLAYGROUND
    PLAYGROUND_USER_NOT_EXISTS("플레이그라운드 유저 정보를 가져올 수 없습니다.", HttpStatus.NOT_FOUND),
    PLAYGROUND_PROFILE_NOT_EXISTS("플레이그라운드 프로필을 등록하지 않은 유저입니다.", HttpStatus.NOT_FOUND),
    INVALID_PLAYGROUND_CARDINAL_INFO("플레이그라운드 활동 정보가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),

    // OPERATION
    OPERATION_PROFILE_NOT_EXISTS("운영 서비스에 존재하지 않는 회원입니다.", HttpStatus.NOT_FOUND),

    // USER
    USER_NOT_FOUND("존재하지 않는 유저입니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_NICKNAME("사용 중인 닉네임입니다.", HttpStatus.CONFLICT),
    NICKNAME_IS_FULL("사용 가능한 닉네임이 없습니다.", HttpStatus.CONFLICT),
    USER_GENERATION_INFO_NOT_FOUND("기수 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // MISSION
    MISSION_NOT_FOUND("존재하지 않는 미션입니다.", HttpStatus.NOT_FOUND),

    // STAMP
    STAMP_NOT_FOUND("존재하지 않는 스탬프입니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_STAMP("이미 해당 미션에 대한 스탬프가 존재합니다.", HttpStatus.CONFLICT),
    INVALID_STAMP_ACTIVITY_DATE("스탬프 활동 날짜가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_STAMP_CONTENTS("스탬프 내용이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_STAMP_IMAGES("스탬프 이미지가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_STAMP_MISSION_ID("스탬프 미션 ID가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_STAMP_ID("스탬프 ID가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    STAMP_DELETE_FORBIDDEN("자신의 스탬프만 삭제할 수 있습니다.", HttpStatus.FORBIDDEN),

    // NOTIFICATION
    NOTIFICATION_NOT_FOUND("존재하지 않는 알림입니다.", HttpStatus.NOT_FOUND),

    // PUSH_TOKEN
    PUSH_TOKEN_NOT_FOUND_FROM_LOCAL("서비스 내에 존재하지 않는 푸시 토큰입니다.", HttpStatus.NOT_FOUND),

    // POKE
    POKE_HISTORY_NOT_FOUND("해당 찌르기 내역은 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    POKE_MESSAGE_TYPE_NOT_FOUND("해당 찌르기 메시지 타입은 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    POKE_MESSAGE_MUST_NOT_BE_NULL("찌르기 메시지 타입은 필수 값입니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_POKE("이미 찌르기를 보낸 친구입니다.", HttpStatus.CONFLICT),

    // FRIEND
    FRIENDSHIP_NOT_FOUND("해당 친구관계는 존재하지 않습니다.", HttpStatus.NOT_FOUND),

    // ADMIN
    INVALID_APP_ADMIN_PASSWORD("잘못된 앱 어드민 패스워드입니다.", HttpStatus.UNAUTHORIZED),

    // S3
    PRE_SIGNED_URI_ERROR("URL을 생성할 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    //Fortune
    FORTUNE_NOT_FOUND("운세 ID에 해당하는 FortuneWord가 없습니다.", HttpStatus.NOT_FOUND),
    FORTUNE_NOT_FOUND_FROM_USER("유저에게 할당된 오늘의 운세가 없습니다.", HttpStatus.NOT_FOUND),
    SLACK_ERROR("슬랙 메시지 알림 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR );

    private final String message;
    private final HttpStatus httpStatus;
}
