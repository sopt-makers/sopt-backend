package org.sopt.app.application.stamp;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StampInfo {

    @Getter
    @Builder
    @ToString
    public static class Stamp {

        private Long id;
        private String contents;
        private List<String> images;
        private Long userId;
        private Long missionId;
        private String activityDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Stamp from(org.sopt.app.domain.entity.soptamp.Stamp stamp) {
            return StampInfo.Stamp.builder()
                .id(stamp.getId())
                .contents(stamp.getContents())
                .images(stamp.getImages())
                .activityDate(stamp.getActivityDate())
                .missionId(stamp.getMissionId())
                .createdAt(stamp.getCreatedAt())
                .updatedAt(stamp.getUpdatedAt())
                .build();
        }
    }
}
