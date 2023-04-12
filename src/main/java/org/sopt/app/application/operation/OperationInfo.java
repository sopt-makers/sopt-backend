package org.sopt.app.application.operation;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class OperationInfo {

    @Getter
    @Builder
    @ToString
    public static class MainView {

        private String announcement;
        private Double attendanceScore;
    }


    @Getter
    @Setter
    @ToString
    public static class ScoreResponse {

        private Boolean success;
        private String message;
        private Score data;
    }

    @Getter
    @Setter
    @ToString
    public static class Score {

        private Double score;
    }
}
