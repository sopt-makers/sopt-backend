package org.sopt.app.application.user;

import lombok.*;
import org.sopt.app.domain.entity.User;

@Getter
@Builder
public class UserProfile {

    private Long userId;
    private String name;
    // private Long playgroundId;

    public static UserProfile of(User user){
        return UserProfile.builder()
                .userId(user.getId())
                .name(user.getUsername())
                // .playgroundId(user.getPlaygroundId())
                .build();
    }

}
