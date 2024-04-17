package org.sopt.app.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Part {
    PLAN("기획"),
    DESIGN("디자인"),
    ANDROID("안드"),
    IOS("아요"),
    WEB("웹"),
    SERVER("서버"),
    ;

    private final String partName;
}
