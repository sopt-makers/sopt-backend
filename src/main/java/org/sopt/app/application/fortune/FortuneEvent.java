package org.sopt.app.application.fortune;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sopt.app.common.event.Event;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FortuneEvent extends Event {

    private Long userId;

    public static FortuneEvent of(Long userId) {
        return new FortuneEvent(userId);
    }
}
