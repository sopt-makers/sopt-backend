package org.sopt.app.application.slack;

import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.webhook.Payload;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SlackService {

    @Value("${webhook.slack.url}")
    private String SLACK_WEBHOOK_URL;

    private final Slack slackClient = Slack.getInstance();

    public void sendSlackMessage(String title, String message) {
        try{
            slackClient.send(SLACK_WEBHOOK_URL, Payload.builder()
                    .text(title)
                    .attachments(List.of(
                        Attachment.builder()
                                .color("#FF0000")
                                .text(message)
                                .build()
                    ))
                .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
