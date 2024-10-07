package org.sopt.app.application.user;

import lombok.*;

@Getter
@Builder
public class UserProfile {

    private Long userId;
    private String name;
    private Long playgroundId;

}
