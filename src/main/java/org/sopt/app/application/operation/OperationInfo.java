package org.sopt.app.application.operation;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OperationInfo {

    @Getter
    @Builder
    @ToString
    public static class MainView {

        private String announcement;
        private Double attendanceScore;
    }
}
