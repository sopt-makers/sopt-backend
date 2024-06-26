package org.sopt.app.domain.enums;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PlaygroundPart {
    PLAN("기획", "기획"),
    DESIGN("디자인", "디자인"),
    ANDROID("안드로이드", "안드"),
    IOS("iOS", "아요"),
    WEB("웹", "웹"),
    SERVER("서버", "서버"),
    NONE("미상", "선배"),
    ;
    String partName;
    String soptampNickname;

    public static PlaygroundPart findPlaygroundPart(String partName) {
        return Arrays.stream(PlaygroundPart.values())
                .filter(playgroundPart -> playgroundPart.partName.equals(partName))
                .findAny()
                .orElse(PlaygroundPart.NONE);
    }
}
