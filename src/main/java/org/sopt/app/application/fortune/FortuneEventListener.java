package org.sopt.app.application.fortune;

import org.sopt.app.domain.enums.NotificationCategory;
import org.sopt.app.presentation.fortune.FortuneAlarmRequest;
import org.sopt.app.presentation.poke.PokeRequest;
import org.sopt.app.presentation.poke.PokeResponse;

import org.springframework.http.MediaType;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Component
@RequiredArgsConstructor
public class FortuneEventListener {

    private final RestTemplate restTemplate;

    @Value("${makers.push.server}")
    private String baseURI;

    @Value("${makers.push.x-api-key}")
    private String apiKey;

    @Async
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendFortuneAlarm(FortuneEvent fortuneEvent) {
        HttpEntity<FortuneAlarmRequest> entity = new HttpEntity<>(
                createBodyFor(fortuneEvent.getUserId()),
                createHeadersForSend()
        );
        sendRequestToAlarmServer(entity);
    }

    private HttpHeaders createHeadersForSend() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("action", "send");
        headers.add("x-api-key", apiKey);
        headers.add("service", "app");
        headers.add("transactionId", UUID.randomUUID().toString());
        return headers;
    }

    private FortuneAlarmRequest createBodyFor(Long userId) {
        return FortuneAlarmRequest.of(
                List.of(String.valueOf(userId)),
                "오늘의 솝마디",  // 메시지 제목
                "오늘의 솝마디를 확인해보세요!",  // 메시지 내용
                NotificationCategory.NEWS.name(),
                "https://app.dev.sopt.org/api/v2/fortune/word"  // Fortune API로 연결되는 URL
        );
    }

    private ResponseEntity<FortuneAlarmRequest> sendRequestToAlarmServer(HttpEntity<FortuneAlarmRequest> requestEntity) {
        return restTemplate.exchange(
                baseURI,
                HttpMethod.POST,
                requestEntity,
                FortuneAlarmRequest.class
        );
    }
}