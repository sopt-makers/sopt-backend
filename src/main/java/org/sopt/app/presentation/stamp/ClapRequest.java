package org.sopt.app.presentation.stamp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.sopt.app.common.utils.SoptampDeepLinkBuilder;
import org.sopt.app.domain.enums.NotificationCategory;

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

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@Builder(access = AccessLevel.PUBLIC)
	public static class ClapAlarmRequest {

		@Schema(description = "유저 아이디", example = "[1]")
		@NotNull
		private List<String> userIds;

		@Schema(description = "알림 제목")
		@NotNull
		private String title;

		@Schema(description = "알림 내용")
		@NotNull
		private String content;

		@Schema(description = "알림 카테고리")
		@NotNull
		private String category;

		@Schema(description = "딥링크")
		private String deepLink;

		/** 첫 박수 (소유자에게만, isMine=true) */
		public static ClapAlarmRequest ofOwnerClapFirst(Long ownerUserId, Long stampId, String missionTitle,
			String ownerPart, String ownerNickname) {
			return ClapAlarmRequest.builder()
				.userIds(List.of(String.valueOf(ownerUserId)))
				.title(String.format("첫 박수 도착! 💌 ‘%s’ 에 누군가가 박수를 쳤어요 👀", missionTitle))
				.content("""
					내 미션 사진에 누군가 첫 박수를 남겼어요. 짝짝짝짝! 👏

					어떤 솝트인이 박수쳤는 지 확인할 수 있어요!

					서로에게 응원의 박수를 보내며 소통해 보세요!
					""")
				.category(NotificationCategory.NEWS.name())
				.deepLink(SoptampDeepLinkBuilder.buildStampDetailLink(stampId, true, ownerNickname, ownerPart))
				.build();
		}

		/** 100/500번째 박수 (소유자에게만, isMine=true) */
		public static ClapAlarmRequest ofOwnerClap100Or500(Long ownerUserId, Long stampId, int targetClapCount,
			String missionTitle, String ownerName, String ownerPart, String ownerNickname) {
			return ClapAlarmRequest.builder()
				.userIds(List.of(String.valueOf(ownerUserId)))
				.title(String.format("축하해요! [%d]번째 박수를 받았어요 🎉", targetClapCount))
				.content(String.format("""
					[%s] [%s]님의 ‘%s’ 미션 사진이 %d번째 박수를 받았습니다. 짝짝짝짝! 👏

					정말 대단해요! 앞으로도 계속해서 멋진 미션을 인증하고 파트/개인 랭킹을 올려보세요.

					어떤 솝트인이 박수쳤는 지 확인할 수 있어요!

					서로에게 응원의 박수를 보내며 소통해 보세요!
					""", ownerPart, ownerName, missionTitle, targetClapCount))
				.category(NotificationCategory.NEWS.name())
				.deepLink(SoptampDeepLinkBuilder.buildStampDetailLink(stampId, true, ownerNickname, ownerPart))
				.build();
		}

		/** 1000 단위 박수 (소유자에게만, isMine=true) */
		public static ClapAlarmRequest ofOwnerClapKilo(Long ownerUserId, Long stampId, int targetClapCount,
			String missionTitle, String ownerPart, String ownerNickname) {
			return ClapAlarmRequest.builder()
				.userIds(List.of(String.valueOf(ownerUserId)))
				.title(String.format("박수 누적 [%d]개 🎉 ‘%s’에 박수 갈채를 받고 있어요.", targetClapCount, missionTitle))
				.content(String.format("""
					미션 ‘%s’ 사진이 %d번째 박수를 받았습니다. 짝짝짝짝! 👏

					정말 대단해요! 앞으로도 계속해서 멋진 미션을 인증하고 파트/개인 랭킹을 올려보세요.

					어떤 솝트인이 박수쳤는 지 확인할 수 있어요!

					서로에게 응원의 박수를 보내며 소통해 보세요!
					""", missionTitle, targetClapCount))
				.category(NotificationCategory.NEWS.name())
				.deepLink(SoptampDeepLinkBuilder.buildStampDetailLink(stampId, true, ownerNickname, ownerPart))
				.build();
		}
	}
}
