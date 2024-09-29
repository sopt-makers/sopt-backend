package org.sopt.app.common.utils;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class HttpHeadersUtils {

    @Value("${makers.push.x-api-key}")
    private String apiKey;

    @Bean
    public HttpHeaders createHeadersForSend() {
        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("action", "send");
        headers.add("x-api-key", apiKey);
        headers.add("service", "app");
        headers.add("transactionId", UUID.randomUUID().toString());
        return headers;
    }
}
