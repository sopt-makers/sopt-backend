package org.sopt.app.common.config;

import feign.*;
import feign.codec.*;
import feign.jackson.*;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

import org.sopt.app.application.meeting.CrewClient;
import org.sopt.app.application.platform.PlatformClient;
import org.springframework.context.annotation.*;
import org.sopt.app.application.playground.PlaygroundClient;
import org.sopt.app.common.external.feign.Feign401ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Configuration
@EnableFeignClients
public class ClientConfig {

	@Value("${makers.playground.server}")
	private String playgroundEndPoint;

	@Value("${makers.crew.server}")
	private String crewEndPoint;

	@Value("${external.auth.url}")
	private String platformEndPoint;

	@Bean
	public PlaygroundClient playgroundClient() {
		return Feign.builder()
			.client(okHttpClient())
			.encoder(encoder())
			.decoder(decoder())
			.errorDecoder(errorDecoder())
			.logger(new Slf4jLogger(PlaygroundClient.class))
			.logLevel(feignLoggerLevel())
			.target(PlaygroundClient.class, playgroundEndPoint);
	}

	@Bean
	public CrewClient crewClient() {
		return Feign.builder()
			.client(okHttpClient())
			.encoder(encoder())
			.decoder(decoder())
			.errorDecoder(errorDecoder())
			.logger(new Slf4jLogger(CrewClient.class))
			.logLevel(feignLoggerLevel())
			.target(CrewClient.class, crewEndPoint);
	}

	@Bean
	public PlatformClient platformClient() {
		return Feign.builder()
			.client(okHttpClient())
			.encoder(encoder())
			.decoder(decoder())
			.errorDecoder(errorDecoder())
			.logger(new Slf4jLogger(PlatformClient.class))
			.logLevel(feignLoggerLevel())
			.target(PlatformClient.class, platformEndPoint);
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
	public ErrorDecoder errorDecoder() {
		return new Feign401ErrorDecoder();
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
