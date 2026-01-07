package org.sopt.app.presentation.appjamtamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.app.domain.enums.TeamNumber;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppjamtampResponse {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class AppjamMissionResponse {

        @Schema(description = "미션 아이디", example = "1")
        private Long id;
        @Schema(description = "미션 제목", example = "팀원 칭찬하기")
        private String title;
        @Schema(description = "미션 수행자 닉네임", example = "보핏아무개")
        private String ownerName;
        @Schema(description = "미션 레벨", example = "1")
        private Integer level;
        @Schema(description = "미션 프로필 이미지", example = "null")
        private List<String> profileImage;
        @Schema(description = "미션 완료 여부", example = "true")
        private Boolean isCompleted;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class AppjamMissionResponses {

        @Schema(description = "요청자의 팀 번호", example = "FIRST")
        private TeamNumber myTeamNumber;
        @Schema(description = "앱잼 참여 여부", example = "true")
        private boolean isAppjamJoined;
        @Schema(description = "팀 번호", example = "FIRST")
        private TeamNumber teamNumber;
        @Schema(description = "팀 이름", example = "보핏")
        private String teamName;
        @Schema(description = "미션 정보")
        private List<AppjamMissionResponse> missions;

        @JsonProperty("isAppjamJoined")
        public boolean isAppjamJoined() {
            return isAppjamJoined;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class AppjamtampView {

        @Schema(description = "스탬프 아이디", example = "1")
        private Long id;
        @Schema(description = "스탬프 내용", example = "모각공했다!")
        private String contents;
        @Schema(description = "스탬프 이미지", example = "[https://s3.ap-northeast-2.amazonaws.com/example/283aab53-22e3-46da-85ec-146c99f82ed4.jpeg]")
        private List<String> images;
        @Schema(description = "활동 날짜", example = "2024.04.08")
        private String activityDate;
        @Schema(description = "스탬프 생성 일시", example = "2023-03-29T18:39:42.106369")
        private LocalDateTime createdAt;
        @Schema(description = "스탬프 수정 일시", example = "2023-03-29T18:39:42.106369")
        private LocalDateTime updatedAt;
        @Schema(description = "미션 아이디", example = "3")
        private Long missionId;
        @Schema(description = "팀 번호", example = "FIRST")
        private TeamNumber teamNumber;
        @Schema(description = "팀 이름", example = "보핏")
        private String teamName;
        @Schema(description = "앱잼탬프 주인 닉네임", example = "서버홍길동")
        private String ownerNickname;
        @Schema(description = "앱잼탬프 주인 프로필 이미지", example = "example.com")
        private String ownerProfileImage;
        @Schema(description = "총 박수 횟수", example = "124")
        private int clapCount;
        @Schema(description = "조회수", example = "58")
        private int viewCount;
        @Schema(description = "내 스탬프인지 여부", example = "false")
        private boolean isMine;
        @Schema(description = "해당 스탬프에 대한 내 박수 횟수", example = "33")
        private int myClapCount;

        @JsonProperty("isMine")
        public boolean isMine() {
            return isMine;
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class StampMain {

        @Schema(description = "스탬프 아이디", example = "1")
        private Long id;
        @Schema(description = "스탬프 내용", example = "모각공했다!")
        private String contents;
        @Schema(description = "스탬프 이미지", example = "[https://s3.ap-northeast-2.amazonaws.com/example/283aab53-22e3-46da-85ec-146c99f82ed4.jpeg]")
        private List<String> images;
        @Schema(description = "활동 날짜", example = "2024.04.08")
        private String activityDate;
        @Schema(description = "스탬프 생성 일시", example = "2023-03-29T18:39:42.106369")
        private LocalDateTime createdAt;
        @Schema(description = "스탬프 수정 일시", example = "2023-03-29T18:39:42.106369")
        private LocalDateTime updatedAt;
        @Schema(description = "앱잼탬프 주인 닉네임", example = "서버홍길동")
        private String ownerNickname;
        @Schema(description = "앱잼탬프 주인 프로필 이미지", example = "example.com")
        private String ownerProfileImage;
        @Schema(description = "미션 아이디", example = "3")
        private Long missionId;
        @Schema(description = "총 박수 횟수", example = "124")
        private int clapCount;
        @Schema(description = "조회수", example = "58")
        private int viewCount;
    }
}
