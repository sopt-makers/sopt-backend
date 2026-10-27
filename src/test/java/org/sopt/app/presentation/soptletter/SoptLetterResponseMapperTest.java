package org.sopt.app.presentation.soptletter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.sopt.app.application.soptletter.SoptLetterInfo;
import org.sopt.app.presentation.soptletter.dto.SoptLetterResponse;

class SoptLetterResponseMapperTest {

    private final SoptLetterResponseMapper mapper = new SoptLetterResponseMapperImpl();

    @Test
    @DisplayName("온보딩 프로필 매핑 시 currentGeneration이 파라미터로 주입되고 나머지는 info에서 매핑된다")
    void of_mapsCurrentGeneration() {
        SoptLetterInfo.Profile profile = SoptLetterInfo.Profile.builder()
            .nickname("익명의 고래")
            .isOnboarded(true)
            .build();

        SoptLetterResponse.OnboardingProfileResponse response = mapper.of(profile, 37L);

        assertThat(response.nickname()).isEqualTo("익명의 고래");
        assertThat(response.isOnboarded()).isTrue();
        assertThat(response.currentGeneration()).isEqualTo(37L);
    }

    @Nested
    @DisplayName("주제 응답은 subtitle과 세 플래그를 그대로 옮긴다")
    class TopicMapping {

        @Test
        @DisplayName("SUCCESS_플래그 값이 서로 달라도 각자 맞는 필드로 옮긴다")
        void mapsSubtitleAndFlags() {
            // given isHot만 false로 두고 나머지 두 플래그는 true로 둔다. isHot을 다른 플래그와 바꿔 옮기는 매핑이면 드러난다
            LocalDateTime createdAt = LocalDateTime.of(2026, 10, 1, 12, 0);
            SoptLetterInfo.TopicSummary summary = SoptLetterInfo.TopicSummary.builder()
                .topicId(3L)
                .title("38기 앱잼 회고")
                .subtitle("앱잼 끝나고 하고 싶은 말")
                .isDefault(true)
                .isNew(true)
                .isHot(false)
                .createdAt(createdAt)
                .build();

            // when
            SoptLetterResponse.TopicResponse response = mapper.of(summary);

            // then subtitle과 세 플래그는 물론 나머지 필드도 값이 그대로다
            assertThat(response.topicId()).isEqualTo(3L);
            assertThat(response.title()).isEqualTo("38기 앱잼 회고");
            assertThat(response.subtitle()).isEqualTo("앱잼 끝나고 하고 싶은 말");
            assertThat(response.isDefault()).isTrue();
            assertThat(response.isNew()).isTrue();
            assertThat(response.isHot()).isFalse();
            assertThat(response.createdAt()).isEqualTo(createdAt);
        }

        @Test
        @DisplayName("SUCCESS_subtitle이 없으면 null로 옮긴다")
        void mapsNullSubtitle() {
            // given subtitle을 비우고 isNew와 isHot은 앞 경우와 뒤집어 둔다. 플래그를 한 값으로 고정한 매핑이면 드러난다
            SoptLetterInfo.TopicSummary summary = SoptLetterInfo.TopicSummary.builder()
                .topicId(4L)
                .title("38기 회고")
                .subtitle(null)
                .isDefault(true)
                .isNew(false)
                .isHot(true)
                .createdAt(LocalDateTime.of(2026, 10, 2, 12, 0))
                .build();

            // when
            SoptLetterResponse.TopicResponse response = mapper.of(summary);

            // then subtitle은 null이고 세 플래그는 넣은 값 그대로다
            assertThat(response.subtitle()).isNull();
            assertThat(response.isDefault()).isTrue();
            assertThat(response.isNew()).isFalse();
            assertThat(response.isHot()).isTrue();
        }
    }

    @Nested
    @DisplayName("안 읽은 수")
    class UnreadCount {

        @Nested
        @DisplayName("주제 응답은 안 읽은 수를 그대로 옮긴다")
        class UnreadCountMapping {

            @Tag("wiring")
            @Test
            @DisplayName("SUCCESS_안 읽은 수를 그대로 옮긴다")
            void mapsUnreadCount() {
                // given 안 읽은 수를 0이 아닌 7로 둔다. 기본값 0을 내보내는 매핑이면 드러난다
                SoptLetterInfo.TopicSummary summary = SoptLetterInfo.TopicSummary.builder()
                    .topicId(3L)
                    .title("38기 앱잼 회고")
                    .unreadCount(7)
                    .createdAt(LocalDateTime.of(2026, 10, 1, 12, 0))
                    .build();

                // when
                SoptLetterResponse.TopicResponse response = mapper.of(summary);

                // then 응답의 unreadCount가 7이다
                assertThat(response.unreadCount()).isEqualTo(7);
            }
        }
    }
}
