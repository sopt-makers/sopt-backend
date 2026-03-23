package org.sopt.app.common.config;

import static org.sopt.app.common.config.AsyncConfig.CACHE_SYNC_EXECUTOR;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SyncTaskExecutor;

@Profile("lambda")
@Configuration
public class SyncConfig {

    @Bean(name = CACHE_SYNC_EXECUTOR)
    public Executor syncTaskExecutor() {
        return new SyncTaskExecutor();
    }
}
