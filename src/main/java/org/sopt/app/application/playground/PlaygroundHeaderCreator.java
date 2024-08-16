package org.sopt.app.application.playground;

import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlaygroundHeaderCreator {

    private static String internalPlaygroundToken;

    @Value("${makers.playground.access-token}")
    private void setInternalPlaygroundToken(String internalPlaygroundToken) {
        this.internalPlaygroundToken = internalPlaygroundToken;
    }

    public static Map<String, String> createAuthorizationHeaderByUserPlaygroundToken(String userPlaygroundToken) {
        return createAuthorizationHeader(userPlaygroundToken);
    }

    public static Map<String, String> createAuthorizationHeaderByInternalPlaygroundToken() {
        return createAuthorizationHeader(internalPlaygroundToken);
    }

    private static Map<String, String> createAuthorizationHeader(String playgroundToken) {
        Map<String, String> headers = createDefaultHeader();
        headers.put(HttpHeaders.AUTHORIZATION, playgroundToken);
        return headers;
    }

    public static Map<String, String> createDefaultHeader() {
        return new HashMap<>(Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }

}
