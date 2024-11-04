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
    }
}
