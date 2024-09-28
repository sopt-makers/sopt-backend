package org.sopt.app.common.config;


import feign.Feign;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import org.sopt.app.application.playground.PlaygroundClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableFeignClients
public class ClientConfig {
    @Value("${makers.playground.server}")
    private String playgroundEndPoint;

    @Bean
    public PlaygroundClient playgroundClient() {
        return Feign.builder()
                .client(okHttpClient())
                .encoder(encoder())
                .decoder(decoder())
                .logger(new Slf4jLogger(PlaygroundClient.class))
                .logLevel(feignLoggerLevel())
                .target(PlaygroundClient.class, playgroundEndPoint);
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
