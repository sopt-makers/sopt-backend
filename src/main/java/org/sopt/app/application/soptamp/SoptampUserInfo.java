package org.sopt.app.application.soptamp;

import lombok.*;
import org.sopt.app.domain.entity.soptamp.SoptampUser;
import org.sopt.app.domain.enums.SoptPart;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SoptampUserInfo {

    private Long id;
    private Long userId;
    private String profileMessage;
    private Long totalPoints;
    private String nickname;
    private SoptPart part;

    public static SoptampUserInfo of(SoptampUser soptampUser) {
        return SoptampUserInfo.builder()
                .id(soptampUser.getId())
                .userId(soptampUser.getUserId())
                .profileMessage(soptampUser.getProfileMessage())
                .totalPoints(soptampUser.getTotalPoints())
                .nickname(soptampUser.getNickname())
                .part(soptampUser.getPart())
                .build();
    }
}
