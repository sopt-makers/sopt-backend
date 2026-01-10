package org.sopt.app.presentation.appjamrank;

import java.time.LocalDateTime;
import java.util.List;

import org.sopt.app.domain.enums.TeamNumber;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppjamRankResponse {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PUBLIC)
	public static class AppjamtampRankResponse {

		@Schema(description = "스탬프 아이디", example = "1")
		private Long stampId;

		@Schema(description = "미션 아이디", example = "2")
		private Long missionId;

		@Schema(description = "유저 아이디", example = "123")
		private Long userId;

		@Schema(description = "스탬프 이미지 (첫 번째 이미지)", example = "https://image.example.com/stamp.jpg")
		private String imageUrl;

		@Schema(description = "스탬프 생성 시간", example = "2025-12-11T12:34:56")
		private LocalDateTime createdAt;

		@Schema(description = "업로드한 유저 이름", example = "이지훈")
		private String userName;

		@Schema(description = "업로드한 유저 프로필 이미지", example = "https://image.example.com/profile.jpg")
		private String userProfileImage;

		@Schema(description = "앱잼 팀 이름", example = "로코코")
		private String teamName;

		@Schema(description = "앱잼 팀 번호", example = "FIRST")
		private TeamNumber teamNumber;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PUBLIC)
	public static class AppjamtampRankListResponse {

		@Schema(description = "최근 인증한 앱잼 스탬프 TOP3 목록")
		private List<AppjamtampRankResponse> ranks;
	}

	@Schema(description = "앱잼 팀 오늘의 득점 랭킹 응답")
	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PUBLIC)
	public static class AppjamTodayTeamRankResponse {

		@Schema(description = "랭킹 순위", example = "1")
		private int rank;

		@Schema(description = "팀 이름", example = "로코코")
		private String teamName;

		@Schema(description = "팀 넘버 enum", example = "FIRST")
		private TeamNumber teamNumber;

		@Schema(description = "오늘 획득한 점수", example = "1000")
		private long todayPoints;

		@Schema(description = "누적 점수", example = "3000")
		private long totalPoints;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PUBLIC)
	public static class AppjamTodayRankListResponse {

		@Schema(description = "오늘의 앱잼 팀 랭킹 TOP10 목록")
		private List<AppjamTodayTeamRankResponse> ranks;
	}
}
