package org.sopt.app.application.stamp;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sopt.app.common.event.Event;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ClapEvent extends Event {

    private final Long ownerUserId;
    private final Long stampId;

    public static ClapEvent of(Long ownerUserId, Long stampId) {
        return new ClapEvent(ownerUserId, stampId);
    }
}
