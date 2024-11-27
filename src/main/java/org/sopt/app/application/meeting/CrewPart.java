package org.sopt.app.application.meeting;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CrewPart {
    PM("기획"),
    DESIGN("디자인"),
    WEB("웹"),
    IOS("iOS"),
    ANDROID("안드로이드"),
    SERVER("서버")
    ;

    private final String partName;
}
