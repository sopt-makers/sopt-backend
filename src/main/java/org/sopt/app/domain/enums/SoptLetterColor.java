package org.sopt.app.domain.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SoptLetterColor {

    BLUE("#C8E1FF"),
    YELLOW("#FFF4D4"),
    GREEN("#CCFFEC"),
    RED("#FFD1D3"),
    ;

    private final String hexCode;
}
