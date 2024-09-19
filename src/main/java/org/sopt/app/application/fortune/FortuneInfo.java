package org.sopt.app.application.fortune;

import lombok.Builder;
import org.sopt.app.domain.entity.fortune.Fortune;

@Builder
public record FortuneInfo(
    Long id,
    String name,
    String title,
    Long fortuneCardId
) {
    public static FortuneInfo of(Fortune fortune) {
        return FortuneInfo.builder()
                .id(fortune.getId())
                .name(fortune.getName())
                .title(fortune.getTitle())
                .fortuneCardId(fortune.getFortuneCardId())
                .build();
    }
}
