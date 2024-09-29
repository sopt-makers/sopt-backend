package org.sopt.app.common.config;

import java.time.Duration;
import java.util.Map;
import lombok.val;
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
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableRetry
class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .additionalInterceptors(clientHttpRequestInterceptor())
                .build();
    }

    public ClientHttpRequestInterceptor clientHttpRequestInterceptor() {
        return (request, body, execution) -> {
            val retryTemplate = new RetryTemplate();
            val retryPolicy = new SimpleRetryPolicy(2, Map.of(
                    HttpServerErrorException.class, true,
                    HttpClientErrorException.class, false
            ));
            retryTemplate.setRetryPolicy(retryPolicy);
            val backOffPolicy = new FixedBackOffPolicy();
            backOffPolicy.setBackOffPeriod(2000);
            retryTemplate.setBackOffPolicy(backOffPolicy);
            try {
                return retryTemplate.execute(context -> execution.execute(request, body));
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        };
    }
}