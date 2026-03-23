package org.sopt.app.application.soptamp;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.rank.CachedUserInfo;
import org.sopt.app.application.rank.RankCacheService;
import org.sopt.app.application.soptamp.SoptampEvent.SoptampUserAllCacheSyncEvent;
import org.sopt.app.application.soptamp.SoptampEvent.SoptampUserProfileCacheSyncEvent;
import org.sopt.app.application.soptamp.SoptampEvent.SoptampUserRemoveCacheSyncEvent;
import org.sopt.app.application.soptamp.SoptampEvent.SoptampUserScoreCacheSyncEvent;
import org.sopt.app.application.user.UserWithdrawEvent;
import org.sopt.app.interfaces.postgres.SoptampUserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SoptampEventListener {

    private final RankCacheService rankCacheService;
    private final SoptampUserRepository soptampUserRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleScoreCacheSyncEvent(final SoptampUserScoreCacheSyncEvent event) {
        soptampUserRepository.findByUserId(event.getUserId()).ifPresent(user ->
            rankCacheService.updateScore(user.getUserId(), user.getTotalPoints())
        );
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProfileCacheSyncEvent(final SoptampUserProfileCacheSyncEvent event) {
        soptampUserRepository.findByUserId(event.getUserId()).ifPresent(user ->
            rankCacheService.updateCachedUserInfo(user.getUserId(), CachedUserInfo.of(SoptampUserInfo.of(user)))
        );
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAllCacheSyncEvent(final SoptampUserAllCacheSyncEvent event) {
        soptampUserRepository.findByUserId(event.getUserId()).ifPresent(user -> {
            rankCacheService.updateScore(user.getUserId(), user.getTotalPoints());
            rankCacheService.updateCachedUserInfo(user.getUserId(), CachedUserInfo.of(SoptampUserInfo.of(user)));
        });
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRemoveCacheSyncEvent(final SoptampUserRemoveCacheSyncEvent event) {
        rankCacheService.removeRank(event.getUserId());
        rankCacheService.removeCachedUserInfo(event.getUserId());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(
        phase = TransactionPhase.AFTER_COMMIT,
        classes = UserWithdrawEvent.class
    )
    public void handleUserWithdrawCache(final UserWithdrawEvent event) {
        rankCacheService.removeRank(event.getUserId());
        rankCacheService.removeCachedUserInfo(event.getUserId());
    }

}
