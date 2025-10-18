package org.sopt.app.presentation.stamp;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.domain.entity.soptamp.Clap;
import org.springframework.data.domain.Page;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClapResponse {

	@Getter
	@AllArgsConstructor(access = AccessLevel.PUBLIC)
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class AddClapResponse {
		@Schema(description = "스탬프 아이디", example = "123")
		private Long stampId;

		@Schema(description = "이번 요청으로 실제 반영된 증가량(0..요청값)", example = "5")
		private int appliedCount;

		@Schema(description = "스탬프 총 박수 합계(상한 없음)", example = "203")
		private int totalClapCount;
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PUBLIC)
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class ClapUsersPage {
		private Page<Clap> page;
		private Map<Long, SoptampUserInfo> profiles;
		private Map<Long, String> imageMap;
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PUBLIC)
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class ClapUserList {

		@Schema(description = "박수 친 유저 목록")
		private List<ClapUserProfile> users;

		@Schema(description = "총 페이지 수", example = "5")
		private int totalPageSize;

		@Schema(description = "현재 페이지 크기", example = "20")
		private Integer pageSize;

		@Schema(description = "현재 페이지 번호(0부터 시작)", example = "0")
		private Integer pageNum;
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PUBLIC)
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class ClapUserProfile {

		@Schema(description = "닉네임", example = "서버이지훈")
		private String nickname;

		@Schema(description = "프로필 이미지 URL", example = "https://cdn.sopt.org/profile/1024.jpg")
		private String profileImageUrl;

		@Schema(description = "프로필 한마디", example = "뒹굴뒹굴 ~,~")
		private String profileMessage;

		@Schema(description = "해당 스탬프에 박수친 횟수 (0~50)", example = "12")
		private int clapCount;
	}
}
