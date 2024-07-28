package org.sopt.app.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.app.application.slack.SlackService;

class SlackServiceTest {

    @Test
    @DisplayName("SUCCESS_슬랙 메시지 보내기")
    void SUCCESS_sendSlackMessage() {
        assertDoesNotThrow(() -> SlackService.sendSlackMessage("title","message"));
    }
}
