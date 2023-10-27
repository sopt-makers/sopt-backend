package org.sopt.app.common.config;


import feign.*;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import org.sopt.app.application.auth.client.PlaygroundClient;
import org.sopt.app.application.operation.client.OperationClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {
    @Value("${makers.playground.server}")
    private String playgroundEndPoint;

    @Value("${makers.operation.server}")
    private String operationEndPoint;

    @Value("${makers.push.server}")

    @Bean
    public PlaygroundClient playgroundClient() {
        return Feign.builder()
                .retryer(retryer())
                .client(okHttpClient())
                .encoder(encoder())
                .decoder(decoder())
                .logger(new Slf4jLogger(PlaygroundClient.class))
                .logLevel(feignLoggerLevel())
                .target(PlaygroundClient.class, playgroundEndPoint);
    }

    @Bean
    public OperationClient operationClient() {
        return Feign.builder()
                .retryer(retryer())
                .client(okHttpClient())
                .encoder(encoder())
                .decoder(decoder())
                .logger(new Slf4jLogger(OperationClient.class))
                .logLevel(feignLoggerLevel())
                .target(OperationClient.class, operationEndPoint);
    }
    @Bean
    public Retryer retryer() {
        return Retryer.NEVER_RETRY;
    }
    @Bean
    public Encoder encoder() {
        return new JacksonEncoder();
    }
    @Bean
    public Decoder decoder() {
        return new JacksonDecoder();
    }
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.HEADERS;
    }
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }

}
