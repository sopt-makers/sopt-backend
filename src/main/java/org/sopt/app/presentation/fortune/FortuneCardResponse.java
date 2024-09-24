package org.sopt.app.presentation.fortune;

import lombok.Builder;
import org.sopt.app.application.fortune.FortuneCardInfo;

@Builder
public record FortuneCardResponse(
        String name,
        String description,
        String imageUrl,
        String imageColorCode
)
{
    public static FortuneCardResponse of(FortuneCardInfo fortuneCardInfo) {
        return FortuneCardResponse.builder()
                .name(fortuneCardInfo.name())
                .description(fortuneCardInfo.description())
                .imageUrl(fortuneCardInfo.imageUrl())
                .imageColorCode(fortuneCardInfo.imageColorCode())
                .build();
    }
}

