package org.sopt.app.domain.enums;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SoptPart {
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
    // 파트장이 솝탬프 파트별 랭킹에 관여할 수 있으려면 각 파트의 shortedPartName이 접두사로 필요하다

    NONE("미상", "선배"),
    ;
    final String partName;
    final String shortedPartName;

    public static SoptPart findPlaygroundPartByPartName(String partName) {
        return Arrays.stream(SoptPart.values())
                .filter(playgroundPart -> playgroundPart.partName.equalsIgnoreCase(partName))
                .findAny()
                .orElse(SoptPart.NONE);
    }

    public static Part toPart(SoptPart soptPart) {
        return switch (soptPart) {
            case PLAN, PLAN_PART_LEADER -> Part.PLAN;
            case DESIGN, DESIGN_PART_LEADER -> Part.DESIGN;
            case ANDROID, ANDROID_PART_LEADER -> Part.ANDROID;
            case IOS, IOS_PART_LEADER -> Part.IOS;
            case WEB, WEB_PART_LEADER -> Part.WEB;
            case SERVER, SERVER_PART_LEADER -> Part.SERVER;
            default -> null;
        };
    }
}
