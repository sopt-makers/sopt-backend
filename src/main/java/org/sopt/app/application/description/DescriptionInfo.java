package org.sopt.app.application.description;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Deprecated
public class DescriptionInfo {

    @Getter
    @Builder
    @ToString
    public static class MainDescription {
        private String topDescription;
        private String bottomDescription;
    }
}
