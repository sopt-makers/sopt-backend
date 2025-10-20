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
    private final int oldClapTotal;
    private final int newClapTotal;

    public static ClapEvent of(Long ownerUserId, Long stampId, int oldClapTotal, int newClapTotal) {
        return new ClapEvent(ownerUserId, stampId, oldClapTotal, newClapTotal);
    }
}
