package org.sopt.app.domain.entity.soptletter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.util.ReflectionTestUtils;

class SoptLetterTopicTest {

    @ParameterizedTest
    @CsvSource({"-1, false", "0, true", "60, true", "61, false"})
    @DisplayName("SUCCESS_시작과 마감 시각을 포함한 기간에만 활성 상태다")
    void SUCCESS_isActiveAt_boundaries(long seconds, boolean expected) {
        LocalDateTime startedAt = LocalDateTime.of(2026, 10, 29, 11, 0);
        SoptLetterTopic topic = new SoptLetterTopic();
        ReflectionTestUtils.setField(topic, "startedAt", startedAt);
        ReflectionTestUtils.setField(topic, "endedAt", startedAt.plusSeconds(60));

        assertThat(topic.isActiveAt(startedAt.plusSeconds(seconds))).isEqualTo(expected);
    }

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

    @Nested
    @DisplayName("NEW 뱃지는 생성 시각부터 7일 동안 붙는다")
    class IsNewAt {

        @Test
        @DisplayName("SUCCESS_생성 후 7일이 되기 1초 전이면 NEW다")
        void newOneSecondBeforeSevenDays() {
            // given 생성 시각을 고정하고 7일 경계 바로 안쪽 시각으로 판정한다
            LocalDateTime createdAt = LocalDateTime.of(2026, 10, 1, 12, 0);
            SoptLetterTopic topic = new SoptLetterTopic();
            ReflectionTestUtils.setField(topic, "createdAt", createdAt);

            // when
            boolean result = topic.isNewAt(createdAt.plusDays(7).minusSeconds(1));

            // then 아직 NEW다
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("SUCCESS_생성 후 정확히 7일이 지나면 NEW가 아니다")
        void notNewAtExactlySevenDays() {
            // given 정확히 7일이 되는 시각으로 판정한다. 경계를 포함하는 구현이면 NEW로 남는다
            LocalDateTime createdAt = LocalDateTime.of(2026, 10, 1, 12, 0);
            SoptLetterTopic topic = new SoptLetterTopic();
            ReflectionTestUtils.setField(topic, "createdAt", createdAt);

            // when
            boolean result = topic.isNewAt(createdAt.plusDays(7));

            // then NEW가 아니다
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("SUCCESS_생성 후 8일이 지나면 NEW가 아니다")
        void notNewAfterEightDays() {
            // given 경계를 하루 넘긴 시각으로 판정한다
            LocalDateTime createdAt = LocalDateTime.of(2026, 10, 1, 12, 0);
            SoptLetterTopic topic = new SoptLetterTopic();
            ReflectionTestUtils.setField(topic, "createdAt", createdAt);

            // when
            boolean result = topic.isNewAt(createdAt.plusDays(8));

            // then NEW가 아니다
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("SUCCESS_생성 시각이 없으면 NEW가 아니다")
        void notNewWhenCreatedAtNull() {
            // given 생성 시각을 넣지 않은 주제다. 저장하기 전 엔티티가 이 상태다
            SoptLetterTopic topic = new SoptLetterTopic();

            // when
            boolean result = topic.isNewAt(LocalDateTime.of(2026, 10, 1, 12, 0));

            // then 예외 없이 NEW가 아니다
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("SUCCESS_생성 시각이 현재보다 미래여도 NEW다")
        void newWhenCreatedAtInFuture() {
            // given 생성 시각을 현재보다 하루 뒤로 둔다. 경과 시간이 음수인 경우다
            LocalDateTime now = LocalDateTime.of(2026, 10, 1, 12, 0);
            SoptLetterTopic topic = new SoptLetterTopic();
            ReflectionTestUtils.setField(topic, "createdAt", now.plusDays(1));

            // when
            boolean result = topic.isNewAt(now);

            // then NEW로 본다
            assertThat(result).isTrue();
        }
    }
}
