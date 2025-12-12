package org.sopt.app.presentation.user;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.sopt.app.application.app_service.dto.AppServiceInfo;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.PlaygroundProfile;
import org.sopt.app.domain.enums.SoptPart;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {

    @Getter
    @Builder
    @ToString
    public static class MainView {

        @Schema(description = "유저 인증 정보", example = "")
        private Playground user;
        @Schema(description = "유저 운영 서비스 정보", example = "")
        private Operation operation;
        @Schema(description = "알림 전체 읽음 여부", example = "false")
        private Boolean isAllConfirm;

        public static MainView unauthenticatedMainView() {
            return MainView.builder()
                    .user(Playground.unauthenticatedUser())
                    .operation(Operation.defaultOperation())
                    .isAllConfirm(false)
                    .build();
        }
    }

    @Getter
    @Builder
    @ToString
    public static class Playground {

        @Schema(description = "활동/비활동/비회원 분기 처리", example = "ACTIVE")
        private String status;
        @Schema(description = "유저 이름", example = "김앱짱")
        private String name;
        @Schema(description = "유저 프로필 메세지", example = "1등이 되고 말거야!")
        private String profileImage;
        @Schema(description = "유저 활동 기수 정보", example = "[32,30,29]")
        private List<Long> generationList;

        public static Playground unauthenticatedUser() {
            return Playground.builder()
                    .status("UNAUTHENTICATED")
                    .name("")
                    .profileImage("")
                    .generationList(List.of())
                    .build();
        }

    }

    @Getter
    @Builder
    @ToString
    public static class Operation {

        @Schema(description = "유저 솝트 출석 정보", example = "2.0")
        private Double attendanceScore;
        @Schema(description = "솝트 공지", example = "공지다!")
        private String announcement;

        public static Operation defaultOperation() {
            return Operation.builder()
                    .attendanceScore(0D)
                    .announcement("")
                    .build();
        }
    }

    @Getter
    @Builder
    @ToString
    public static class Soptamp {

        @Schema(description = "유저 닉네임", example = "김앱짱")
        private String nickname;
        @Schema(description = "유저 랭킹 점수", example = "15")
        private Long points;
        @Schema(description = "유저 프로필 메세지", example = "1등이 되고 말거야!")
        private String profileMessage;
    }

    @Getter
    @Builder
    @ToString
    public static class ProfileMessage {

        @Schema(description = "유저 프로필 메세지", example = "1등이 되고 말거야!")
        private String profileMessage;
    }

    @Getter
    @Builder
    @ToString
    public static class Generation {

        @Schema(description = "현재 솝트 기수", example = "33")
        private Long currentGeneration;
        @Schema(description = "활동/비활동/비회원 분기 처리", example = "ACTIVE")
        private String status;
    }

    @Getter
    @Builder
    @ToString
    public static class AppService {

        @Schema(description = "앱 서비스 네임", example = "SOPTAMP/POKE")
        private String serviceName;
        @Schema(description = "활동 기수 유저", example = "true")
        private Boolean activeUser;
        @Schema(description = "비활동 기수 유저", example = "false")
        private Boolean inactiveUser;

        public static AppService of(final AppServiceInfo appServiceInfo) {
            return AppService.builder()
                    .serviceName(appServiceInfo.getServiceName())
                    .activeUser(appServiceInfo.getActiveUser())
                    .inactiveUser(appServiceInfo.getInactiveUser())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class SoptLog {
        @Schema(description = "유저 이름", example = "차은우")
        private String userName;
        @Schema(description = "프로필 이미지 url", example = "www.png")
        private String profileImage;
        @Schema(description = "파트")
        private String part;
        @Schema(description = "콕찌르기 횟수")
        private String pokeCount;
        @Schema(description = "", example = "14등")
        private String soptampRank;
        @Schema(description = "솝력 ", example = "LV.7")
        private String soptLevel;
        @Schema(description = "유저 소개", example = "false")
        private String profileMessage;
        @Schema(description = "솝트와", example = "37개월")
        private String during;
        @Schema(description = "아이콘 리스트", example = "['www.png']")
        private List<String> icons;
        @Schema(description = "활동 기수 여부", example = "true")
        private Boolean isActive;
        @Schema(description = "운세 체크 여부", example = "true")
        private Boolean isFortuneChecked;
        @Schema(description = "오늘의 운세", example = "오늘은 좋은 날이에요!")
        private String todayFortuneText;

        public static SoptLog of(int soptLevel, Long pokeCount, Long soptampRank, Long during, Boolean isActive,
                                 List<String> icons,
                                 PlaygroundProfile playgroundProfile,boolean partTypeToKorean,
                                 boolean isFortuneChecked, String fortuneText) {
            return SoptLog.builder()
                    .soptLevel("Lv." + soptLevel)
                    .pokeCount(pokeCount + "회")
                    .soptampRank(soptampRank != null ? soptampRank + "등" : "공개 예정!")
                    .userName(playgroundProfile.getName())
                    .profileImage(playgroundProfile.getProfileImage() != null ? playgroundProfile.getProfileImage() : "")
                    .part(playgroundProfile.getAllActivities().stream()
                            .map(c -> c.getPlaygroundPart().getPartName())
                            .filter(c -> !c.equals(SoptPart.NONE.getPartName()))
                            .collect(Collectors.joining("/")))
                    .profileMessage(playgroundProfile.getIntroduction() != null ? playgroundProfile.getIntroduction() : "")
                    .during(during != null ? during + "개월" : "")
                    .isActive(isActive)
                    .icons(icons)
                    .isFortuneChecked(isFortuneChecked)
                    .todayFortuneText(fortuneText)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Create {
        @Schema(description = "생성된 유저 ID", example = "101")
        private Long userId;
    }

    @JsonInclude(Include.NON_NULL)
    public record MySoptLog(

        @Schema(description = "활동 기수 여부")
        boolean isActive,

        @Schema(description = "오늘의 운세 확인 여부")
        boolean isFortuneChecked,

        @Schema(description = "오늘의 운세 텍스트")
        String todayFortuneText,

        @Schema(description = "총 완료한 미션 개수")
        Integer soptampCount,

        @Schema(description = "완료한 미션 총 조회수")
        Integer viewCount,

        @Schema(description = "총 받은 박수 개수")
        Integer myClapCount,

        @Schema(description = "총 쳐준 박수 개수")
        Integer clapCount,

        @Schema(description = "총 콕찌르기 개수 (활동 기수만 반환)")
        int totalPokeCount,

        @Schema(description = "친한 친구 콕 찌르기 횟수의 총합 (2~4회 찌른 사람 대상, 활동 기수만)")
        int newFriendsPokeCount,

        @Schema(description = "단짝 친구 콕 찌르기 횟수의 총합 (5~10회 찌른 사람 대상, 활동 기수만)")
        int bestFriendsPokeCount,

        @Schema(description = "천생 연분 콕 찌르기 횟수의 총합 (11회부터 찌른 사람 대상, 활동 기수만)")
        int soulmatesPokeCount
    ) {

        public static MySoptLog ofInactive(
            boolean isFortuneChecked,
            String todayFortuneText,
            int totalPokeCount,
            int newFriendsPokeCount,
            int bestFriendsPokeCount,
            int soulmatesPokeCount
        ) {
            return new MySoptLog(
                false,
                isFortuneChecked,
                todayFortuneText,
                null,
                null,
                null,
                null,
                totalPokeCount,
                newFriendsPokeCount,
                bestFriendsPokeCount,
                soulmatesPokeCount
            );
        }

        public static MySoptLog ofActive(
            boolean isFortuneChecked,
            String todayFortuneText,
            int soptampCount,
            int viewCount,
            int myClapCount,
            int clapCount,
            int totalPokeCount,
            int newFriendsPokeCount,
            int bestFriendsPokeCount,
            int soulmatesPokeCount
        ) {
            return new MySoptLog(
                true,
                isFortuneChecked,
                todayFortuneText,
                soptampCount,
                viewCount,
                myClapCount,
                clapCount,
                totalPokeCount,
                newFriendsPokeCount,
                bestFriendsPokeCount,
                soulmatesPokeCount
            );
        }
    }
}
