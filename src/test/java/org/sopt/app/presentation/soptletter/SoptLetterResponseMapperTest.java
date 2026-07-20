package org.sopt.app.presentation.soptletter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
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
}
