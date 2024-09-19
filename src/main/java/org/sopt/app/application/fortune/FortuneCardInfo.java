package org.sopt.app.application.fortune;

import lombok.Builder;
import org.sopt.app.domain.entity.fortune.FortuneCard;

@Builder
public record FortuneCardInfo(
        Long id,
        String name,
        String description,
        String imageUrl
) {
    public static FortuneCardInfo of(FortuneCard fortuneCard) {
        return FortuneCardInfo.builder()
                .id(fortuneCard.getId())
                .name(fortuneCard.getName())
                .description(fortuneCard.getDescription())
                .imageUrl(fortuneCard.getImageUrl())
                .build();
    }
}
