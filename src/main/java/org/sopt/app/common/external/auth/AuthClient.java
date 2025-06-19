package org.sopt.app.common.external.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.app.common.exception.ClientException;
import org.sopt.app.common.response.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthClient {

    private final WebClient authWebClient;
    private final AuthClientProperty authProperty;

    public String getJwk() {
        try {
            return authWebClient.get()
                    .uri(authProperty.endpoints().jwk())
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorMap(WebClientResponseException.class, ex -> {
                        log.error("Failed to receive response from Auth server: {}", ex.getResponseBodyAsString(), ex);
                        return new ClientException(ErrorCode.RESPONSE_ERROR);
                    })
                    .block();
        } catch (RuntimeException e) {
            log.error("Unexpected exception occurred during Auth server communication: {}", e.getMessage(), e);
            throw new ClientException(ErrorCode.COMMUNICATION_ERROR);
        }
    }
}