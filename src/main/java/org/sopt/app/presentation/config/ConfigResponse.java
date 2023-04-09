package org.sopt.app.presentation.config;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class ConfigResponse {

    @Getter
    @Builder
    @ToString
    public static class Availability {

        private Boolean isAvailable;

    }
}
