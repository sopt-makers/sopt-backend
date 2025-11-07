package org.sopt.app.application.stamp;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    }

    @Getter
    @Builder
    @ToString
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class StampView {

        private Long id;
        private String contents;
        private List<String> images;
        private Long userId;
        private Long missionId;
        private String ownerNickName;
        private String activityDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private int clapCount;
        private int viewCount;
        private boolean isMine;
        private int myClapCount;

        public static StampView of(Stamp stamp, int myClapCount, boolean isMine, String ownerNickName) {
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
}
