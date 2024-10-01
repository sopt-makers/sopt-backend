package org.sopt.app.application.slack;

import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.webhook.Payload;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SlackService {

    @Value("${webhook.slack.url}")
    private String SLACK_WEBHOOK_URL;
    private static final Slack slackClient = Slack.getInstance();

    public void sendSlackMessage(List<Attachment> attachments) {
        try {
            slackClient.send(SLACK_WEBHOOK_URL, Payload.builder()
                    .text("에러 알림")
                    .attachments(attachments)
                    .build());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
