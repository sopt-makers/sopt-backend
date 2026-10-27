package org.sopt.app.application.appservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.appservice.dto.AppServiceBadgeInfo;
import org.sopt.app.application.soptletter.SoptLetterService;

@ExtendWith(MockitoExtension.class)
class SoptLetterBadgeManagerTest {

    private static final Long USER_ID = 1L;

    @Mock
    private SoptLetterService soptLetterService;

    @InjectMocks
    private SoptLetterBadgeManager soptLetterBadgeManager;

    @Nested
    @DisplayName("홈 뱃지")
    class AcquireAppServiceBadgeInfo {

        @Nested
        @DisplayName("홈 화면의 솝레터 뱃지는 솝레터 뱃지 매니저가 만든다")
        class Wiring {

            @Tag("wiring")
            @Test
            @DisplayName("SUCCESS_솝레터 앱 서비스는 솝레터 뱃지 매니저를 쓴다")
            void soptLetterUsesSoptLetterBadgeManager() {
                // given 홈 앱 서비스 목록의 솝레터다

                // when
                String badgeManagerName = AppServiceName.SOPT_LETTER.getBadgeManagerName();

                // then 솝레터 뱃지 매니저의 빈 이름을 가리킨다
                assertThat(badgeManagerName).isEqualTo("soptLetterBadgeManager");
            }
        }

        @Nested
        @DisplayName("합계를 두 자리까지 보여 준다")
        class TwoDigits {

            @ParameterizedTest
            @CsvSource(useHeadersInDisplayName = true, textBlock = """
                합계, 뱃지 켬, 문자열
                0, false, ''
                1, true, 1
                99, true, 99
                100, true, 99+
                """)
            @DisplayName("SUCCESS_안 읽은 수의 합을 두 자리까지 보여 준다")
            void showsUpToTwoDigits(long unreadCount, boolean display, String badge) {
                // given 안 읽은 수의 합이 경계값 0, 1, 99, 100이다. 뱃지가 꺼지고 켜지는 자리와 두 자리를 넘는 자리를 모두 본다
                when(soptLetterService.getTotalUnreadCount(USER_ID)).thenReturn(unreadCount);

                // when
                AppServiceBadgeInfo result = soptLetterBadgeManager.acquireAppServiceBadgeInfo(USER_ID);

                // then 표의 뱃지 켬과 문자열을 보낸다
                assertThat(result.getDisplayAlarmBadge()).isEqualTo(display);
                assertThat(result.getAlarmBadge()).isEqualTo(badge);
            }
        }
    }
}
