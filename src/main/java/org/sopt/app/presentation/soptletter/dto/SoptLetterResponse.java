package org.sopt.app.presentation.soptletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SoptLetterResponse {

    @Schema(description = "솝레터 온보딩 프로필 정보")
    public record OnboardingProfileResponse(
        @Schema(description = "닉네임", example = "익명의 달달한 간장게장")
        String nickname,
        @Schema(description = "온보딩 완료 여부", example = "false")
        boolean isOnboarded
    ) {
    }

    @Schema(description = "솝레터 개별 주제 메시지 작성 응답")
    public record WriteMessageResponse(
        @Schema(description = "생성된 메시지 ID", example = "125")
        Long messageId,
        @Schema(description = "주제 ID", example = "3")
        Long topicId,
        @Schema(description = "작성자 익명 닉네임", example = "반짝이는 고래")
        String authorNickname,
        @Schema(description = "저장된 메시지 본문", example = "앱잼 때 같이 밤새면서 고생했던 게 아직도 기억나...")
        String content,
        @Schema(description = "메세지 색상 hex code", example = "#CCFFEC")
        String colorCode,
        @Schema(description = "회전 각도", example = "10.0")
        Double rotationDegree,
        @Schema(description = "모양 타입", example = "POINT")
        String shapeType,
        @Schema(description = "생성 시각")
        java.time.LocalDateTime createdAt,
        @Schema(description = "수정 시각")
        java.time.LocalDateTime updatedAt,
        @Schema(description = "초기 좋아요 수", example = "0")
        Integer likeCount,
        @Schema(description = "내가 좋아요를 눌렀는지 여부", example = "false")
        Boolean likedByMe,
        @Schema(description = "내가 작성한 메시지 여부", example = "true")
        Boolean mine
    ) {
    }
}
