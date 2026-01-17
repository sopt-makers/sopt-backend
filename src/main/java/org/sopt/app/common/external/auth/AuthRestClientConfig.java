package org.sopt.app.common.external.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class AuthRestClientConfig {

    public static final String HEADER_API_KEY = "X-Api-Key";
    public static final String HEADER_SERVICE_NAME = "X-Service-Name";
    private static final int TIMEOUT_MILLIS = 5000;

    @Bean
    public RestClient authWebClient(AuthClientProperty property) {

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(TIMEOUT_MILLIS);
        requestFactory.setReadTimeout(TIMEOUT_MILLIS);

        return RestClient.builder()
            .baseUrl(property.url())
            .requestFactory(requestFactory) // 타임아웃 설정 적용
            .defaultHeader(HEADER_API_KEY, property.apiKey())
            .defaultHeader(HEADER_SERVICE_NAME, property.serviceName())
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

}
