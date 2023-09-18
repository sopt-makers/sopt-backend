package org.sopt.app.application.description;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class DescriptionInfo {

    @Getter
    @Builder
    @ToString
    public static class MainDescription {
        private String topDescription;
        private String bottomDescription;
    }
}
