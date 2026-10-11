package org.sopt.app.presentation.admin.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.sopt.app.common.exception.UnauthorizedException;
import org.sopt.app.facade.AdminNotificationFacade;

class SoptLetterReminderRequestTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void blankOrMissingFieldsAreRejectedWithKoreanMessages() throws Exception {
        var request = mapper.readValue("{\"notificationTitle\":\" \"}", AdminNotificationRequest.SoptLetterUnwrittenReminder.class);
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            assertThat(factory.getValidator().validate(request)).extracting(v -> v.getMessage())
                .containsExactlyInAnyOrder("알림 제목은 필수입니다.", "알림 내용은 필수입니다.");
        }
    }

    @Test
    void validRequestForwardsLinksAndReturnsEmptySuccess() throws Exception {
        var facade = mock(AdminNotificationFacade.class);
        var controller = new AdminNotificationController(facade);
        var request = mapper.readValue("{\"notificationTitle\":\"제목\",\"notificationContent\":\"내용\",\"deepLink\":\"sopt://sopt-letter\"}", AdminNotificationRequest.SoptLetterUnwrittenReminder.class);
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            assertThat(factory.getValidator().validate(request)).isEmpty();
        }
        var response = controller.sendSoptLetterUnwrittenReminder(request, "test-key", "test-key");
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNull();
        verify(facade).sendSoptLetterUnwrittenReminder("제목", "내용", "sopt://sopt-letter");
    }

    @Test
    void invalidApiKeyDoesNotSend() throws Exception {
        var facade = mock(AdminNotificationFacade.class);
        var controller = new AdminNotificationController(facade);
        var request = mapper.readValue("{\"notificationTitle\":\"제목\",\"notificationContent\":\"내용\"}", AdminNotificationRequest.SoptLetterUnwrittenReminder.class);
        assertThatThrownBy(() -> controller.sendSoptLetterUnwrittenReminder(request, "wrong", "test-key"))
            .isInstanceOf(UnauthorizedException.class);
        verifyNoInteractions(facade);
    }
}
