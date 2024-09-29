package org.sopt.app.application.soptamp;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.sopt.app.domain.entity.soptamp.SoptampUser;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SoptampUserInfo {

    private Long id;
    private Long userId;
    private String profileMessage;
    private Long totalPoints;
    private String nickname;

    public static SoptampUserInfo of(SoptampUser soptampUser) {
        return SoptampUserInfo.builder()
                .id(soptampUser.getId())
                .userId(soptampUser.getUserId())
                .profileMessage(soptampUser.getProfileMessage())
                .totalPoints(soptampUser.getTotalPoints())
                .nickname(soptampUser.getNickname())
                .build();
    }
}
