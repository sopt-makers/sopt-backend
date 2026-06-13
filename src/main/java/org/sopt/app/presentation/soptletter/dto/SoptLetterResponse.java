package org.sopt.app.presentation.soptletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
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

    @Schema(description = "솝레터 익명 신고 폼 주소 조회 응답")
    public record ReportFormResponse(
        @Schema(description = "익명 신고 폼 URL", example = "https://example.com/sopt-letter-report")
        String reportFormUrl
    ) {
    }

    @Schema(description = "솝레터 주제 목록 조회 응답")
    public record TopicsResponse(
        @Schema(description = "주제 목록")
        List<TopicResponse> topics
    ) {
    }

    @Schema(description = "솝레터 주제 응답")
    public record TopicResponse(
        @Schema(description = "주제 ID", example = "3")
        Long topicId,
        @Schema(description = "주제 제목", example = "36기 회고")
        String title,
        @Schema(description = "기본 주제 여부", example = "false")
        boolean isDefault,
        @Schema(description = "주제 생성 시각")
        LocalDateTime createdAt
    ) {
    }

    @Schema(description = "솝레터 주제 단일 조회 응답")
    public record TopicDetailResponse(
        @Schema(description = "주제 ID", example = "3")
        Long topicId,
        @Schema(description = "주제 제목", example = "36기 회고")
        String title,
        @Schema(description = "현재 메인 CTA 노출 기간 내 활성 주제인지 여부", example = "true")
        Boolean active,
        @Schema(description = "주제 CTA 노출 시작 시각")
        LocalDateTime startedAt,
        @Schema(description = "주제 CTA 노출 종료 시각")
        LocalDateTime endedAt,
        @Schema(description = "주제 생성 시각")
        LocalDateTime createdAt
    ) {
    }

    @Schema(description = "개별 주제 솝레터 메시지 목록 조회 응답")
    public record TopicMessagesResponse(
        @Schema(description = "주제 ID", example = "3")
        Long topicId,
        @Schema(description = "주제 제목", example = "36기 회고")
        String title,
        @Schema(description = "해당 주제 메시지 개수", example = "2")
        Integer totalCount,
        @Schema(description = "다음 페이지 조회 커서", example = "91")
        Long nextCursor,
        @Schema(description = "다음 페이지 존재 여부", example = "false")
        Boolean hasNext,
        @Schema(description = "해당 주제 메시지 목록")
        List<TopicMessageResponse> messages
    ) {
    }

    @Schema(description = "개별 주제 솝레터 메시지 목록 아이템")
    public record TopicMessageResponse(
        @Schema(description = "메시지 ID", example = "91")
        Long messageId,
        @Schema(description = "작성자 익명 닉네임", example = "반짝이는 고래")
        String authorNickname,
        @Schema(description = "공백 포함 50자 미리보기", example = "앱잼 때 같이 밤새면서 고생했던 게 아직도 기억나...")
        String previewContent,
        @Schema(description = "메모 색상 hex code", example = "#CCFFEC")
        String colorCode,
        @Schema(description = "메모 회전 각도", example = "4.0")
        Double rotationDegree,
        @Schema(description = "메모 모양 타입", example = "POINT")
        String shapeType,
        @Schema(description = "생성 시각")
        LocalDateTime createdAt,
        @Schema(description = "수정 시각")
        LocalDateTime updatedAt,
        @Schema(description = "좋아요 수", example = "5")
        Integer likeCount,
        @Schema(description = "내가 좋아요 눌렀는지 여부", example = "false")
        Boolean likedByMe,
        @Schema(description = "내가 작성한 메시지인지 여부", example = "false")
        Boolean mine
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
        LocalDateTime createdAt,
        @Schema(description = "수정 시각")
        LocalDateTime updatedAt,
        @Schema(description = "초기 좋아요 수", example = "0")
        Integer likeCount,
        @Schema(description = "내가 좋아요를 눌렀는지 여부", example = "false")
        Boolean likedByMe,
        @Schema(description = "내가 작성한 메시지 여부", example = "true")
        Boolean mine
    ) {
    }

    @Schema(description = "솝레터 개별 메시지 상세 조회 응답")
    public record MessageDetailResponse(
        @Schema(description = "메시지 ID", example = "124")
        Long messageId,
        @Schema(description = "주제 ID", example = "3")
        Long topicId,
        @Schema(description = "작성자 익명 닉네임", example = "반짝이는 고래")
        String authorNickname,
        @Schema(description = "메시지 전체 내용", example = "이번 기수 동안 같이해서 너무 즐거웠어. 항상 응원할게!")
        String content,
        @Schema(description = "메모 색상 hex code", example = "#FFF4D4")
        String colorCode,
        @Schema(description = "메모 회전 각도", example = "0.0")
        Double rotationDegree,
        @Schema(description = "메모 모양 타입", example = "CLOUD")
        String shapeType,
        @Schema(description = "생성 시각")
        LocalDateTime createdAt,
        @Schema(description = "수정 시각")
        LocalDateTime updatedAt,
        @Schema(description = "좋아요 수", example = "0")
        Integer likeCount,
        @Schema(description = "내가 좋아요 눌렀는지 여부", example = "false")
        Boolean likedByMe,
        @Schema(description = "내가 작성한 메시지인지 여부", example = "true")
        Boolean mine
    ) {
    }
}
