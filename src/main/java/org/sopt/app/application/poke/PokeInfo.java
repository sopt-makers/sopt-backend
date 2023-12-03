package org.sopt.app.application.poke;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class PokeInfo {

    @Getter
    @Builder
    @ToString
    public static class Activity {
        private final int generation;
        private final String part;
    }

}
