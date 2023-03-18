package org.sopt.app.presentation.mission;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class MissionRequest {

    @Getter
    @Setter
    @ToString
    public static class RegisterMissionRequest {

        private String title; // 미션 제목
        private Integer level; // 미션 레벨
    }
}
