package org.sopt.app.presentation.admin.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.app.common.exception.UnauthorizedException;
import org.sopt.app.facade.AdminNotificationFacade;

class SoptLetterNewTopicRequestTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("FAIL_새 주제 알림 제목과 내용이 비어 있으면 검증에 실패한다")
    void FAIL_sendNewTopic_blankFields() throws Exception {
        // given
        var request = mapper.readValue("{\"notificationTitle\":\" \"}", AdminNotificationRequest.SoptLetterNewTopic.class);

        // when & then
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            assertThat(factory.getValidator().validate(request)).extracting(v -> v.getMessage())
                .containsExactlyInAnyOrder("알림 제목은 필수입니다.", "알림 내용은 필수입니다.");
        }
    }

    @Test
    @DisplayName("SUCCESS_새 주제 알림 요청을 전달하고 본문 없는 성공 응답을 반환한다")
    void SUCCESS_sendNewTopic() throws Exception {
        // given
        var facade = mock(AdminNotificationFacade.class);
        var controller = new AdminNotificationController(facade);
        var request = mapper.readValue("{\"notificationTitle\":\"제목\",\"notificationContent\":\"내용\",\"deepLink\":\"sopt://sopt-letter\"}", AdminNotificationRequest.SoptLetterNewTopic.class);

        // when
        var response = controller.sendSoptLetterNewTopic(request, "test-key", "test-key");

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNull();
        verify(facade).sendSoptLetterNewTopic("제목", "내용", "sopt://sopt-letter");
    }

    @Test
    @DisplayName("FAIL_새 주제 알림 API 키가 다르면 발송하지 않는다")
    void FAIL_sendNewTopic_invalidApiKey() throws Exception {
        // given
        var facade = mock(AdminNotificationFacade.class);
        var controller = new AdminNotificationController(facade);
        var request = mapper.readValue("{\"notificationTitle\":\"제목\",\"notificationContent\":\"내용\"}", AdminNotificationRequest.SoptLetterNewTopic.class);

        // when & then
        assertThatThrownBy(() -> controller.sendSoptLetterNewTopic(request, "wrong", "test-key"))
            .isInstanceOf(UnauthorizedException.class);
        verifyNoInteractions(facade);
    }
}
