package org.sopt.app.application.fortune;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.app.common.utils.HttpHeadersUtils;
import org.sopt.app.domain.enums.NotificationCategory;
import org.sopt.app.presentation.fortune.FortuneAlarmRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class FortuneEventListener {

    private final RestTemplate restTemplate;
    private final HttpHeadersUtils headersUtils;

    @Value("${makers.push.server}")
    private String baseURI;

    @EventListener
    public void sendFortuneAlarm(FortuneEvent fortuneEvent) {
        HttpEntity<FortuneAlarmRequest> entity = new HttpEntity<>(
                createBodyFor(fortuneEvent.getUserId()),
                headersUtils.createHeadersForSend()
        );
        this.sendRequestToAlarmServer(entity);
    }

    private FortuneAlarmRequest createBodyFor(Long userId) {
        return FortuneAlarmRequest.builder()
                .userIds(List.of(String.valueOf(userId)))
                .title("오늘의 솝마디")
                .content("오늘의 솝마디를 확인해보세요!")
                .category(NotificationCategory.NEWS.name())
                .deepLink("home/fortune")
                .build();
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