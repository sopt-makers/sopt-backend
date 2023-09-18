package org.sopt.app.presentation.description;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class DescriptionResponse {

    @Getter
    @Setter
    @ToString
    @Builder
    public static class MainDescription {
        private String topDescription;
        private String bottomDescription;
    }
}
