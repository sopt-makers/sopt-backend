package org.sopt.app.common.client.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.sopt.app.common.client.notification.dto.request.InstantAlarmRequest;
import org.sopt.app.common.client.notification.dto.response.AlarmResponse;
import org.sopt.app.common.client.notification.dto.response.InstantAlarmResponse;
import org.sopt.app.common.utils.HttpHeadersUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Component
@RequiredArgsConstructor
public class InstantlyAlarmSender implements AlarmSender<InstantAlarmRequest> {

    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpHeadersUtils headersUtils;

    @Value("${makers.push.server}")
    private String baseURI;

    @Override
    public AlarmResponse send(InstantAlarmRequest request) {
        try {
            if(request.getUserIds().isEmpty()){
                return InstantAlarmResponse.empty();
            }
            val entity = new HttpEntity<>(request, headersUtils.createHeadersForSend());
            ResponseEntity<InstantAlarmResponse> exchange = restTemplate.exchange(
                baseURI,
                HttpMethod.POST,
                entity,
                InstantAlarmResponse.class
            );
            return exchange.getBody();
        } catch (Exception e) {
            log.warn("instant alarm send failed: body={}", request, e);
            throw e;
        }
    }

}
