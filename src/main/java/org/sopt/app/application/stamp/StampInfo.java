package org.sopt.app.application.stamp;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.sopt.app.application.appjamuser.AppjamUserInfo.TeamSummary;
import org.sopt.app.domain.enums.TeamNumber;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StampInfo {

    @Getter
    @Builder
    @ToString
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Stamp {

        private Long id;
        private String contents;
        private List<String> images;
        private Long userId;
        private Long missionId;
        private String activityDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private int clapCount;
        private int viewCount;

        public static StampInfo.Stamp from(org.sopt.app.domain.entity.soptamp.Stamp stamp) {
            return Stamp.builder()
                .id(stamp.getId())
                .contents(stamp.getContents())
                .images(stamp.getImages())
                .userId(stamp.getUserId())
                .missionId(stamp.getMissionId())
                .activityDate(stamp.getActivityDate())
                .createdAt(stamp.getCreatedAt())
                .updatedAt(stamp.getUpdatedAt())
                .clapCount(stamp.getClapCount())
                .viewCount(stamp.getViewCount())
                .build();
        }
    }

    @Getter
    @Builder
    @ToString
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class StampView {

        private Long id;
        private String contents;
        private List<String> images;
        private Long missionId;
        private String ownerNickName;
        private String activityDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private int clapCount;
        private int viewCount;
        private boolean isMine;
        private int myClapCount;

        public static StampView of(Stamp stamp, int myClapCount, boolean isMine,
            String ownerNickName) {
            return StampView.builder()
                .id(stamp.getId())
                .contents(stamp.getContents())
                .images(stamp.getImages())
                .activityDate(stamp.getActivityDate())
                .createdAt(stamp.getCreatedAt())
                .updatedAt(stamp.getUpdatedAt())
                .missionId(stamp.getMissionId())
                .ownerNickName(ownerNickName)
                .clapCount(stamp.getClapCount())
                .viewCount(stamp.getViewCount() + 1)
                .isMine(isMine)
                .myClapCount(myClapCount)
                .build();
        }
    }

    @Getter
    @Builder
    @ToString
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AppjamtampView {

        private Long id;
        private String contents;
        private List<String> images;
        private Long missionId;
        private TeamNumber teamNumber;
        private String teamName;
        private String ownerNickName;
        private String ownerProfileImage;
        private String activityDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private int clapCount;
        private int viewCount;
        private boolean isMine;
        private int myClapCount;

        public static AppjamtampView of(
            Stamp stamp,
            int myClapCount,
            boolean isMine,
            String ownerNickName,
            String ownerProfileImage,
            TeamSummary teamSummary
        ) {
            return AppjamtampView.builder()
                .id(stamp.getId())
                .contents(stamp.getContents())
                .images(stamp.getImages())
                .activityDate(stamp.getActivityDate())
                .createdAt(stamp.getCreatedAt())
                .updatedAt(stamp.getUpdatedAt())
                .missionId(stamp.getMissionId())
                .ownerNickName(ownerNickName)
                .ownerProfileImage(ownerProfileImage)
                .teamNumber(teamSummary.getTeamNumber())
                .teamName(teamSummary.getTeamName())
                .clapCount(stamp.getClapCount())
                .viewCount(stamp.getViewCount() + 1)
                .isMine(isMine)
                .myClapCount(myClapCount)
                .build();
        }
    }

    @Getter
    @Builder
    @ToString
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class StampWithProfile {

        private Long id;
        private String contents;
        private List<String> images;
        private Long userId;
        private Long missionId;
        private String activityDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String ownerNickname;
        private String ownerProfileImage;
        private int clapCount;
        private int viewCount;

        public static StampInfo.StampWithProfile of(
            StampInfo.Stamp stamp,
            String ownerNickname,
            String ownerProfileImage
        ) {
            return StampWithProfile.builder()
                .id(stamp.getId())
                .contents(stamp.getContents())
                .images(stamp.getImages())
                .userId(stamp.getUserId())
                .missionId(stamp.getMissionId())
                .activityDate(stamp.getActivityDate())
                .createdAt(stamp.getCreatedAt())
                .updatedAt(stamp.getUpdatedAt())
                .ownerNickname(ownerNickname)
                .ownerProfileImage(ownerProfileImage)
                .clapCount(stamp.getClapCount())
                .viewCount(stamp.getViewCount())
                .build();
        }
    }

}
