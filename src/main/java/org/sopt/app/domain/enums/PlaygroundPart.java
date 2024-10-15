package org.sopt.app.domain.enums;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PlaygroundPart {
    PRESIDENT("회장", "회장"),
    VICE_PRESIDENT("부회장", "부회장"),
    GENERAL_AFFAIR("총무", "총무"),
    MEDIA_TEAM_LEADER("미디어 팀장", "미팀장"),
    OPERATIONS_TEAM_LEADER("운영 팀장", "운팀장"),
    MAKERS_TEAM_LEADER("메이커스 팀장", "메팀장"),

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

    NONE("미상", "선배"),
    ;
    final String partName;
    final String shortedPartName;

    public static PlaygroundPart findPlaygroundPartByPartName(String partName) {
        return Arrays.stream(PlaygroundPart.values())
                .filter(playgroundPart -> playgroundPart.partName.equals(partName))
                .findAny()
                .orElse(PlaygroundPart.NONE);
    }
}
