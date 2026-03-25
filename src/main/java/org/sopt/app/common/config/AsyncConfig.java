package org.sopt.app.common.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@Profile("!lambda")
public class AsyncConfig {

    public static final String CACHE_SYNC_EXECUTOR = "cacheSyncTaskExecutor";

    @Bean(name = CACHE_SYNC_EXECUTOR)
    public Executor cacheSyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("CacheSync-");

        // 큐가 꽉 차면, 이벤트를 발행한 메인 스레드가 직접 동기적으로 처리함
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}