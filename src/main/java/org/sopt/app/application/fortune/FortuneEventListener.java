package org.sopt.app.application.fortune;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.app.common.utils.HttpHeadersUtils;
import org.sopt.app.domain.enums.NotificationCategory;
import org.sopt.app.presentation.fortune.FortuneAlarmRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class FortuneEventListener {

    private final RestTemplate restTemplate;
    private final HttpHeadersUtils headersUtils;


    @Value("${makers.push.server}")
    private String baseURI;

    @Async
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendFortuneAlarm(FortuneEvent fortuneEvent) {
        HttpEntity<FortuneAlarmRequest> entity = new HttpEntity<>(
                createBodyFor(fortuneEvent.getUserId()),
                headersUtils.createHeadersForSend()
        );
        sendRequestToAlarmServer(entity);
    }

    private FortuneAlarmRequest createBodyFor(Long userId) {
        return FortuneAlarmRequest.of(
                List.of(String.valueOf(userId)),
                "오늘의 솝마디",  // 메시지 제목
                "오늘의 솝마디를 확인해보세요!",  // 메시지 내용
                NotificationCategory.NEWS.name(),
                "home/fortune"  // Fortune API로 연결되는 URL
        );
    }

    private ResponseEntity<FortuneAlarmRequest> sendRequestToAlarmServer(
            HttpEntity<FortuneAlarmRequest> requestEntity) {
        return restTemplate.exchange(
                baseURI,
                HttpMethod.POST,
                requestEntity,
                FortuneAlarmRequest.class
        );
    }
}