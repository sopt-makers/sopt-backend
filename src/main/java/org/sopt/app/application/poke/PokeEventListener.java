package org.sopt.app.application.poke;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.common.utils.HttpHeadersUtils;
import org.sopt.app.presentation.poke.PokeRequest.PokeAlarmRequest;
import org.sopt.app.presentation.poke.PokeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestTemplate;

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
            val entity = new HttpEntity<>(
                    PokeAlarmRequest.of(pokeEvent.getPokedUserId()),
                    headersUtils.createHeadersForSend()
            );
            sendRequestToAlarmServer(entity);
    }

    private void sendRequestToAlarmServer(HttpEntity<PokeAlarmRequest> requestEntity) {
        restTemplate.exchange(
                baseURI,
                HttpMethod.POST,
                requestEntity,
                PokeResponse.PokeAlarmStatusResponse.class
        );
    }
}
