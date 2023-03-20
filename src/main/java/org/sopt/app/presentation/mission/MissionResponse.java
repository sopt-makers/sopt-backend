package org.sopt.app.presentation.mission;


import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class MissionResponse {

    @Getter
    @Setter
    @ToString
    public static class Main {

        private Long id;
        private String title;
        private Integer level;
        private List<String> profileImage;
    }

    @Getter
    @Setter
    @ToString
    public static class Completeness {

        private Long id;
        private String title;
        private Integer level;
        private List<String> profileImage;
        private Boolean isCompleted;
    }

    @Getter
    @Setter
    @ToString
    public static class Id {

        private Long missionId;
    }
}