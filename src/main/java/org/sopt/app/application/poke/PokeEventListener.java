package org.sopt.app.application.poke;

import org.sopt.app.common.utils.HttpHeadersUtils;
import org.sopt.app.domain.enums.NotificationCategory;
import org.sopt.app.presentation.poke.PokeRequest;
import org.sopt.app.presentation.poke.PokeResponse;

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

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Component
@RequiredArgsConstructor
public class PokeEventListener {
    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpHeadersUtils headersUtils;

    @Value("${makers.push.server}")
    private String baseURI;

    @Async
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendPokeAlarm(PokeEvent pokeEvent) {
            val entity = new HttpEntity(
                    createBodyFor(pokeEvent.getPokedUserId()),
                    headersUtils.createHeadersForSend()
            );
            sendRequestToAlarmServer(entity);
    }

    private PokeRequest.PokeAlarmRequest createBodyFor(Long pokedUserId) {
        return PokeRequest.PokeAlarmRequest.of(
                List.of(String.valueOf(pokedUserId)),
                "콕 찌르기",
                "누군가가 콕 찔렀어요. 확인해보세요!",
                NotificationCategory.NEWS.name(),
                "home/poke/notification-list"
        );
    }
    private ResponseEntity<PokeResponse.PokeAlarmStatusResponse> sendRequestToAlarmServer(HttpEntity requestEntity) {
        return restTemplate.exchange(
                baseURI,
                HttpMethod.POST,
                requestEntity,
                PokeResponse.PokeAlarmStatusResponse.class
        );
    }
}
