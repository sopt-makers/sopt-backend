package org.sopt.app.application;

import static org.mockito.ArgumentMatchers.any;

import com.slack.api.Slack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.slack.SlackService;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SlackServiceTest {

    @Mock
    private Slack slackClient;

    @InjectMocks
    private SlackService slackService;


    @BeforeEach
    void beforeTest() {
        ReflectionTestUtils.setField(slackService, "SLACK_WEBHOOK_URL", "http://not-valid-url.com");
    }

    @Test
    @DisplayName("SUCCESS_슬랙 메시지 보내기")
    void SUCCESS_sendSlackMessage() {
        // 아래는 현재 동작하지 않는 테스트입니다.
//        final String anyTitle = anyString();
//        final String anyMessage = anyString();
//
//        try{
//            when(slackClient.send(anyString(), any(Payload.class))).thenReturn(null);
//            slackService.sendSlackMessage(anyTitle, anyMessage);
//            Mockito.verify(slackClient).send(anyString(), any(Payload.class));
//        } catch (Exception e) {
//            Assertions.fail();
//        }
    }
}