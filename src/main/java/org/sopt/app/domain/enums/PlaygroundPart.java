package org.sopt.app.domain.enums;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PlaygroundPart {
    PLAN("기획", "기획"),
    PLAN_PART_LEADER("기획 파트장", "기획파트장"),
    DESIGN("디자인", "디자인"),
    DESIGN_PART_LEADER("디자인 파트장", "디자인파트장"),
    ANDROID("안드로이드", "안드"),
    ANDROID_PART_LEADER("안드로이드 파트장", "안드파트장"),
    IOS("iOS", "아요"),
    IOS_PART_LEADER("iOS 파트장", "아요파트장"),
    WEB("웹", "웹"),
    WEB_PART_LEADER("웹 파트장", "웹파트장"),
    SERVER("서버", "서버"),
    SERVER_PART_LEADER("서버 파트장", "서버파트장"),
    PRESIDENT("회장", "회장"),
    VICE_PRESIDENT("부회장", "부회장"),
    NONE("미상", "선배"),
    ;
    final String partName;
    final String soptampNickname;

    public static PlaygroundPart findPlaygroundPart(String partName) {
        return Arrays.stream(PlaygroundPart.values())
                .filter(playgroundPart -> playgroundPart.soptampNickname.equals(partName))
                .findAny()
                .orElse(PlaygroundPart.NONE);
    }
}
