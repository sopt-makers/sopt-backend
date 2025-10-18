package org.sopt.app.presentation.stamp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClapRequest {

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AddClapRequest {
		@Schema(description = "이번 요청에서 증가시킬 박수 수(양수)", example = "7", minimum = "1")
		@Positive(message = "clapCount must be > 0")
		private int clapCount;
	}
}
