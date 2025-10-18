package org.sopt.app.presentation.stamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
