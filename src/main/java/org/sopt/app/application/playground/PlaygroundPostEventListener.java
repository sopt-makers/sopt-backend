package org.sopt.app.application.playground;

import lombok.RequiredArgsConstructor;
import org.sopt.app.common.config.AsyncConfig;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaygroundPostEventListener {

    private final PlaygroundPostRefreshService playgroundPostRefreshService;

    @Async(AsyncConfig.CACHE_SYNC_EXECUTOR)
    @EventListener(PlaygroundRecentPostRefreshEvent.class)
    public void handleRecentPostRefreshEvent(PlaygroundRecentPostRefreshEvent event) {
        playgroundPostRefreshService.refreshRecentPosts();
    }

    @Async(AsyncConfig.CACHE_SYNC_EXECUTOR)
    @EventListener(PlaygroundPopularPostRefreshEvent.class)
    public void handlePopularPostRefreshEvent(PlaygroundPopularPostRefreshEvent event) {
        playgroundPostRefreshService.refreshPopularPosts();
    }
}
