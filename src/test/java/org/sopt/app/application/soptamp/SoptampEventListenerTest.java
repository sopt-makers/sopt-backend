package org.sopt.app.application.soptamp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.rank.CachedUserInfo;
import org.sopt.app.application.rank.RankCacheService;
import org.sopt.app.application.soptamp.SoptampEvent.SoptampUserAllCacheSyncEvent;
import org.sopt.app.application.soptamp.SoptampEvent.SoptampUserProfileCacheSyncEvent;
import org.sopt.app.application.soptamp.SoptampEvent.SoptampUserRemoveCacheEvent;
import org.sopt.app.application.soptamp.SoptampEvent.SoptampUserScoreCacheSyncEvent;
import org.sopt.app.application.user.UserWithdrawEvent;
import org.sopt.app.common.fixtures.SoptampUserFixture;
import org.sopt.app.domain.entity.soptamp.SoptampUser;
import org.sopt.app.interfaces.postgres.SoptampUserRepository;

@ExtendWith(MockitoExtension.class)
class SoptampEventListenerTest {

    @Mock
    private RankCacheService rankCacheService;

    @Mock
    private SoptampUserRepository soptampUserRepository;

    @InjectMocks
    private SoptampEventListener soptampEventListener;

    @Test
    @DisplayName("SUCCESS_점수 동기화 이벤트 수신 시 DB 조회 후 캐시 점수 업데이트 호출")
    void SUCCESS_handleScoreCacheSyncEvent_callUpdateScore() {
        // given
        final Long userId = 1L;
        final SoptampUser soptampUser = SoptampUserFixture.SOPTAMP_USER_1;
        when(soptampUserRepository.findByUserId(userId)).thenReturn(Optional.of(soptampUser));
        
        final SoptampUserScoreCacheSyncEvent event = SoptampUserScoreCacheSyncEvent.of(userId);

        // when
        soptampEventListener.handleScoreCacheSyncEvent(event);

        // then
        verify(soptampUserRepository).findByUserId(userId);
        verify(rankCacheService).updateScore(eq(soptampUser.getUserId()), eq(soptampUser.getTotalPoints().longValue()));
    }

    @Test
    @DisplayName("SUCCESS_프로필 동기화 이벤트 수신 시 DB 조회 후 프로필 캐시 업데이트 호출")
    void SUCCESS_handleProfileCacheSyncEvent_callUpdateCachedUserInfo() {
        // given
        final Long userId = 1L;
        final SoptampUser soptampUser = SoptampUserFixture.SOPTAMP_USER_1;
        when(soptampUserRepository.findByUserId(userId)).thenReturn(Optional.of(soptampUser));
        
        final SoptampUserProfileCacheSyncEvent event = SoptampUserProfileCacheSyncEvent.of(userId);

        // when
        soptampEventListener.handleProfileCacheSyncEvent(event);

        // then
        verify(soptampUserRepository).findByUserId(userId);
        verify(rankCacheService).updateCachedUserInfo(eq(soptampUser.getUserId()), any(CachedUserInfo.class));
    }

    @Test
    @DisplayName("SUCCESS_전체 동기화 이벤트 수신 시 점수와 프로필 모두 업데이트 호출")
    void SUCCESS_handleAllCacheSyncEvent_callBothUpdateMethods() {
        // given
        final Long userId = 1L;
        final SoptampUser soptampUser = SoptampUserFixture.SOPTAMP_USER_1;
        when(soptampUserRepository.findByUserId(userId)).thenReturn(Optional.of(soptampUser));
        
        final SoptampUserAllCacheSyncEvent event = SoptampUserAllCacheSyncEvent.of(userId);

        // when
        soptampEventListener.handleAllCacheSyncEvent(event);

        // then
        verify(soptampUserRepository).findByUserId(userId);
        verify(rankCacheService).updateScore(eq(soptampUser.getUserId()), eq(soptampUser.getTotalPoints().longValue()));
        verify(rankCacheService).updateCachedUserInfo(eq(soptampUser.getUserId()), any(CachedUserInfo.class));
    }

    @Test
    @DisplayName("SUCCESS_캐시 삭제 이벤트 수신 시 랭크 및 프로필 캐시 삭제 호출")
    void SUCCESS_handleRemoveCacheSyncEvent_callRemoveMethods() {
        // given
        final Long userId = 1L;
        final SoptampUserRemoveCacheEvent event = SoptampUserRemoveCacheEvent.of(userId);

        // when
        soptampEventListener.handleRemoveCacheSyncEvent(event);

        // then
        verify(rankCacheService).removeRank(userId);
        verify(rankCacheService).removeCachedUserInfo(userId);
    }

    @Test
    @DisplayName("SUCCESS_탈퇴 이벤트 수신 시 랭크 및 프로필 캐시 삭제 호출")
    void SUCCESS_handleUserWithdrawCache_callRemoveMethods() {
        // given
        final Long userId = 1L;
        final UserWithdrawEvent event = new UserWithdrawEvent(userId);

        // when
        soptampEventListener.handleUserWithdrawCache(event);

        // then
        verify(rankCacheService).removeRank(userId);
        verify(rankCacheService).removeCachedUserInfo(userId);
    }
}