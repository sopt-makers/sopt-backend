package org.sopt.app.application.user;

import lombok.*;
import org.sopt.app.domain.entity.User;

@Getter
@Builder
public class UserProfile {

    private Long userId;
    private String name;
    // private Long playgroundId;

    public static UserProfile of(Long userId, String name){
        return UserProfile.builder()
                .userId(userId)
                .name(name)
                // .playgroundId(user.getPlaygroundId())
                .build();
    }

}
