package org.sopt.app;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.TimeZone;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing // JPA Auditing(감시, 감사) 기능을 활성화 하는 어노테이션 createdDate, modifiedDate 저장 활성화
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class AppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

    @PostConstruct
    void setApplicationTimeZone(){
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
