package org.sopt.app.common.external.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.app.common.exception.ClientException;
import org.sopt.app.common.response.ErrorCode;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthClient {

    private final RestClient authRestClient; // WebClient -> RestClient 변경
    private final AuthClientProperty authProperty;

    public String getJwk() {
        try {
            return authRestClient.get()
                .uri(authProperty.endpoints().jwk())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    String errorBody = new String(response.getBody().readAllBytes());
                    log.error("Failed to receive response from Auth server: {}", errorBody);
                    throw new ClientException(ErrorCode.RESPONSE_ERROR);
                })
                .body(String.class);

        } catch (ClientException e) { // onStatus에서 던진 예외만 그대로 다시 던짐
            throw e;
        } catch (Exception e) {
            log.error("Unexpected exception occurred during Auth server communication: {}",
                e.getMessage(), e);
            throw new ClientException(ErrorCode.COMMUNICATION_ERROR);
        }
    }

}
