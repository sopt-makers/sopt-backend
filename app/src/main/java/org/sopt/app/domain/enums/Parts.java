package org.sopt.app.domain.enums;

import lombok.Getter;

@Getter
public enum Parts {
    ALL("전체"),
    PLAN("기획"),
    DESIGN("디자인"),
    ANDROID("안드로이드"),
    iOS("iOS"),
    SERVER("서버"),
    WEB("웹");


    Parts(String description) {
    }
}
