package org.sopt.app.application.soptletter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.app.domain.entity.soptletter.SoptLetter;
import org.sopt.app.domain.enums.SoptLetterColor;
import org.sopt.app.domain.enums.SoptLetterShapeType;

class SoptLetterGeneratorTest {

    private final SoptLetterGenerator soptLetterGenerator = new SoptLetterGenerator();

    @Test
    @DisplayName("편지 생성 시 전달받은 기본 속성들이 엔티티에 올바르게 설정된다")
    void shouldMapBasicPropertiesCorrectly() {
        // given
        Long authorProfileId = 1L;
        Long topicId = 10L;
        String message = "테스트 편지 내용";

        // when
        SoptLetter result = soptLetterGenerator.generate(authorProfileId, topicId, message, null);

        // then
        assertThat(result.getAuthorProfileId()).isEqualTo(authorProfileId);
        assertThat(result.getTopicId()).isEqualTo(topicId);
        assertThat(result.getMessage()).isEqualTo(message);
        assertThat(result.getLikeCount()).isEqualTo(0);
        assertThat(result.getDegree()).isIn(-10.0, 0.0, 10.0);
        assertThat(result.getShapeType()).isIn((Object[]) SoptLetterShapeType.values());
    }

    @Test
    @DisplayName("이전 편지 색상이 null이면 첫 번째 색상인 BLUE_50이 지정된다")
    void shouldAssignInitialColorWhenPreviousIsNull() {
        // when
        SoptLetter result = soptLetterGenerator.generate(1L, 1L, "테스트", null);

        // then
        assertThat(result.getColor()).isEqualTo(SoptLetterColor.BLUE_50);
    }

    @Test
    @DisplayName("이전 편지 색상에 따라 다음 순서의 색상이 순환하여 지정된다")
    void shouldRotateColorsCorrectly() {
        // BLUE_50 -> GREEN_50
        SoptLetter color1 = soptLetterGenerator.generate(1L, 1L, "테스트", SoptLetterColor.BLUE_50);
        assertThat(color1.getColor()).isEqualTo(SoptLetterColor.GREEN_50);

        // GREEN_50 -> YELLOW_50
        SoptLetter color2 = soptLetterGenerator.generate(1L, 1L, "테스트", SoptLetterColor.GREEN_50);
        assertThat(color2.getColor()).isEqualTo(SoptLetterColor.YELLOW_50);

        // YELLOW_50 -> RED_50
        SoptLetter color3 = soptLetterGenerator.generate(1L, 1L, "테스트", SoptLetterColor.YELLOW_50);
        assertThat(color3.getColor()).isEqualTo(SoptLetterColor.RED_50);

        // RED_50 -> BLUE_50
        SoptLetter color4 = soptLetterGenerator.generate(1L, 1L, "테스트", SoptLetterColor.RED_50);
        assertThat(color4.getColor()).isEqualTo(SoptLetterColor.BLUE_50);
    }
}
