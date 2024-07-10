package org.sopt.app.application.soptamp;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SoptampUserPlaygroundInfo {

    private Long userId;
    private Long playgroundId;
    private String name;
    private Long generation;
    private String part;
}
