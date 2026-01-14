package org.sopt.app.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Profile("!lambda")
public class SchedulingConfig {
    public SchedulingConfig(){
        System.out.println("✅ [DEBUG] 스케줄러 빈이 생성되었습니다! (람다가 아님)");
    }
}
