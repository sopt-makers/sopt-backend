package org.sopt.app.domain.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SoptLetterColor {

    BLUE_50("#C8E1FF"),
    YELLOW_50("#FFF4D4"),
    GREEN_50("#CCFFEC"),
    RED_50("#FFD1D3"),
    ;

    private final String hexCode;
}
