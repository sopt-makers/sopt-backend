package org.sopt.app.application.fortune;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.app.common.utils.HttpHeadersUtils;
import org.sopt.app.domain.enums.NotificationCategory;
import org.sopt.app.presentation.fortune.FortuneAlarmRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class FortuneNotificationSender {

    private final RestTemplate restTemplate;
    private final HttpHeadersUtils headersUtils;

    @Value("${makers.push.server}")
    private String baseURI;

    public void sendFortuneNotification(List<Long> playgroundIds) {
        HttpEntity<FortuneAlarmRequest> entity = new HttpEntity<>(
                FortuneAlarmRequest.of(playgroundIds),
                headersUtils.createHeadersForSend()
        );
        this.sendRequestToAlarmServer(entity);
    }

    private void sendRequestToAlarmServer(HttpEntity<FortuneAlarmRequest> requestEntity) {
        restTemplate.exchange(
                baseURI,
                HttpMethod.POST,
                requestEntity,
                FortuneAlarmRequest.class
        );
    }
}
