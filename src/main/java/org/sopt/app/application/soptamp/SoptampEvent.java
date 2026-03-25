package org.sopt.app.application.soptamp;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.sopt.app.common.event.Event;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SoptampEvent {

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class SoptampUserScoreCacheSyncEvent extends Event {
        private final Long userId;
    }

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class SoptampUserProfileCacheSyncEvent extends Event {
        private final Long userId;
    }

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class SoptampUserAllCacheSyncEvent extends Event {
        private final Long userId;
    }

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class SoptampUserRemoveCacheEvent extends Event {
        private final Long userId;
    }

}
