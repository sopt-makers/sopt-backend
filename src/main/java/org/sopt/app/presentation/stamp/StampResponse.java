package org.sopt.app.presentation.stamp;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class StampResponse {

    @Getter
    @Setter
    @ToString
    public static class Main {

        private Long id;
        private String contents;
        private List<String> images;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Long missionId;
    }

    @Getter
    @Setter
    @ToString
    public static class Id {

        private Long stampId;
    }
}
