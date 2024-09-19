package org.sopt.app.presentation.fortune;

import lombok.Builder;
import org.sopt.app.application.fortune.FortuneInfo;

@Builder
public record FortuneResponse(
        String userName,
        String title
)
{
    public static FortuneResponse of(FortuneInfo fortuneInfo, String userName) {
        return FortuneResponse.builder()
                .title(fortuneInfo.title())
                .userName(userName)
                .build();
    }
}