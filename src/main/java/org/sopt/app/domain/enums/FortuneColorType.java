package org.sopt.app.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FortuneColorType {
    YELLOW("#FFC234","yellow"),
    BLUE("#4485FF","blue"),
    GREEN("#16BF81","green"),
    ORGANE("#F77234","orange");
    private final String colorCode;
    private final String colorType;
}
