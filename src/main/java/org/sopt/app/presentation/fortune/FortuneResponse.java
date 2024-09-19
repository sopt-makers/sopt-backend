package org.sopt.app.presentation.fortune;

import lombok.Builder;
import org.sopt.app.application.fortune.FortuneWordInfo;

@Builder
public record FortuneResponse(
        String userName,
        String title
)
{
    public static FortuneResponse of(FortuneWordInfo fortuneWordInfo, String userName) {
        return FortuneResponse.builder()
                .title(fortuneWordInfo.title())
                .userName(userName)
                .build();
    }
}