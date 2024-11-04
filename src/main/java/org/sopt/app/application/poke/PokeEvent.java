package org.sopt.app.application.poke;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sopt.app.common.event.Event;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PokeEvent extends Event {

    private Long pokedUserId;

    public static PokeEvent of(Long pokedUserId) {
        return new PokeEvent(pokedUserId);
    }
}
