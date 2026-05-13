package org.sopt.app.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.appservice.AppServiceBadgeService;
import org.sopt.app.application.appservice.AppServiceService;
import org.sopt.app.application.appservice.OperationConfigService;
import org.sopt.app.application.description.DescriptionService;
import org.sopt.app.application.meeting.MeetingService;
import org.sopt.app.application.platform.PlatformService;
import org.sopt.app.application.playground.PlaygroundPopularPostRefreshEvent;
import org.sopt.app.application.playground.PlaygroundPostCacheService;
import org.sopt.app.application.playground.PlaygroundRecentPostRefreshEvent;
import org.sopt.app.application.playground.dto.PlaygroundPopularPost;
import org.sopt.app.application.playground.dto.PlaygroundRecentPost;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.common.event.EventPublisher;

@ExtendWith(MockitoExtension.class)
class HomeFacadeTest {

    @Mock
    private DescriptionService descriptionService;
    @Mock
    private PlaygroundPostCacheService playgroundPostCacheService;
    @Mock
    private AppServiceService appServiceService;
    @Mock
    private AppServiceBadgeService appServiceBadgeService;
    @Mock
    private MeetingService meetingService;
    @Mock
    private OperationConfigService operationConfigService;
    @Mock
    private PlatformService platformService;
    @Mock
    private SoptampUserService soptampUserService;
    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private HomeFacade homeFacade;

    @Test
    @DisplayName("최근 게시글 조회 시 캐시가 있으면 캐시된 데이터를 반환한다")
    void getPlaygroundRecentPosts_returnsCachedData_whenCacheExists() {
        // given
        Long userId = 1L;
        List<PlaygroundRecentPost> cachedPosts = List.of(
                new PlaygroundRecentPost(1L, 1L, "img", "name", "34기", "category", "title", "content", "link", "2024-05-13 00:00:00.000000", false)
        );
        when(playgroundPostCacheService.getCachedRecentPosts()).thenReturn(Optional.of(cachedPosts));

        // when
        List<PlaygroundRecentPost> result = homeFacade.getPlaygroundRecentPosts(userId);

        // then
        assertEquals(cachedPosts, result);
        verify(eventPublisher, times(0)).raise(any(PlaygroundRecentPostRefreshEvent.class));
    }

    @Test
    @DisplayName("최근 게시글 조회 시 캐시가 없으면 빈 리스트를 반환하고 이벤트를 발행한다")
    void getPlaygroundRecentPosts_returnsEmptyListAndPublishesEvent_whenCacheIsEmpty() {
        // given
        Long userId = 1L;
        when(playgroundPostCacheService.getCachedRecentPosts()).thenReturn(Optional.empty());

        // when
        List<PlaygroundRecentPost> result = homeFacade.getPlaygroundRecentPosts(userId);

        // then
        assertTrue(result.isEmpty());
        verify(eventPublisher, times(1)).raise(any(PlaygroundRecentPostRefreshEvent.class));
    }

    @Test
    @DisplayName("인기 게시글 조회 시 캐시가 있으면 캐시된 데이터를 반환한다")
    void getPlaygroundPopularPosts_returnsCachedData_whenCacheExists() {
        // given
        Long userId = 1L;
        List<PlaygroundPopularPost> cachedPosts = List.of(
                new PlaygroundPopularPost(1L, 1L, "img", "name", "34기", 1, "category", "title", "content", "link")
        );
        when(playgroundPostCacheService.getCachedPopularPosts()).thenReturn(Optional.of(cachedPosts));

        // when
        List<PlaygroundPopularPost> result = homeFacade.getPlaygroundPopularPosts(userId);

        // then
        assertEquals(cachedPosts, result);
        verify(eventPublisher, times(0)).raise(any(PlaygroundPopularPostRefreshEvent.class));
    }

    @Test
    @DisplayName("인기 게시글 조회 시 캐시가 없으면 빈 리스트를 반환하고 이벤트를 발행한다")
    void getPlaygroundPopularPosts_returnsEmptyListAndPublishesEvent_whenCacheIsEmpty() {
        // given
        Long userId = 1L;
        when(playgroundPostCacheService.getCachedPopularPosts()).thenReturn(Optional.empty());

        // when
        List<PlaygroundPopularPost> result = homeFacade.getPlaygroundPopularPosts(userId);

        // then
        assertTrue(result.isEmpty());
        verify(eventPublisher, times(1)).raise(any(PlaygroundPopularPostRefreshEvent.class));
    }
}
