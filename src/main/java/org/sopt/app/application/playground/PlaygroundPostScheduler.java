package org.sopt.app.application.playground;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaygroundPostScheduler {

    private final PlaygroundPostRefreshService playgroundPostRefreshService;

    @Scheduled(cron = "0 0 * * * *")
    public void refreshPlaygroundPosts() {
        log.info("Playground 게시글 캐시 갱신 시작");
        playgroundPostRefreshService.refreshRecentPosts();
        playgroundPostRefreshService.refreshPopularPosts();
    }
}
