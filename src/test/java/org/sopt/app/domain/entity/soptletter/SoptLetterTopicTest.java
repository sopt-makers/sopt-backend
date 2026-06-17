package org.sopt.app.domain.entity.soptletter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class SoptLetterTopicTest {

    @Test
    @DisplayName("SUCCESS_현재 시각이 주제 노출 기간 내에 있으면 활성 상태를 반환한다")
    void SUCCESS_isActiveAt() {
        // given
        SoptLetterTopic topic = new SoptLetterTopic();
        ReflectionTestUtils.setField(topic, "startedAt", LocalDateTime.of(2026, 4, 18, 0, 0));
        ReflectionTestUtils.setField(topic, "endedAt", LocalDateTime.of(2026, 4, 28, 23, 59, 59));

        // when
        boolean result = topic.isActiveAt(LocalDateTime.of(2026, 4, 20, 12, 0));

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("SUCCESS_종료 시각이 없으면 비활성 상태를 반환한다")
    void SUCCESS_isActiveAt_endedAtNull() {
        // given
        SoptLetterTopic topic = new SoptLetterTopic();
        ReflectionTestUtils.setField(topic, "startedAt", LocalDateTime.of(2026, 4, 18, 0, 0));

        // when
        boolean result = topic.isActiveAt(LocalDateTime.of(2026, 4, 20, 12, 0));

        // then
        assertThat(result).isFalse();
    }
}
