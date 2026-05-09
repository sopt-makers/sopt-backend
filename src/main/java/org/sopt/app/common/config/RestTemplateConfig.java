package org.sopt.app.common.config;

import java.time.Duration;
import java.util.Map;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableRetry
class RestTemplateConfig {

    @Value("${external.api.timeout.connect}")
    private int connectTimeout;

    @Value("${external.api.timeout.read}")
    private int readTimeout;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(connectTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .additionalInterceptors(clientHttpRequestInterceptor())
                .build();
    }

    public ClientHttpRequestInterceptor clientHttpRequestInterceptor() {
        return (request, body, execution) -> {
            val retryTemplate = new RetryTemplate();
            val retryPolicy = new SimpleRetryPolicy(2, Map.of(
                    HttpServerErrorException.class, true,
                    HttpClientErrorException.class, false,
                    ResourceAccessException.class, true
            ));
            retryTemplate.setRetryPolicy(retryPolicy);
            val backOffPolicy = new FixedBackOffPolicy();
            backOffPolicy.setBackOffPeriod(2000);
            retryTemplate.setBackOffPolicy(backOffPolicy);
            try {
                return retryTemplate.execute(context -> execution.execute(request, body));
            } catch (RuntimeException e) {
                throw e; // 원본 유지
            } catch (Exception e) {
                throw new RestClientException("request failed", e);
            }
        };
    }
}