package org.sopt.app.application.fortune;

import lombok.Builder;
import org.sopt.app.domain.entity.fortune.FortuneWord;

@Builder
public record FortuneWordInfo(
    Long id,
    String title,
    Long fortuneCardId
) {
    public static FortuneWordInfo of(FortuneWord fortuneWord) {
        return FortuneWordInfo.builder()
                .id(fortuneWord.getId())
                .title(fortuneWord.getTitle())
                .fortuneCardId(fortuneWord.getFortuneCardId())
                .build();
    }
}
