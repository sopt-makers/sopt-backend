package org.sopt.app.application.soptamp;

import java.util.EnumSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sopt.app.domain.enums.SoptPart;

/**
 * auth DB의 part/role/team 코드값을 앱 도메인의 SoptPart 이름으로 변환한다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class AuthSoptPartMapper {

    /**
     * soptamp_user.part CHECK 제약에 포함되지 않는 auth 활동 파트는 NONE으로 처리한다.
     */
    private static final Set<SoptPart> UNSUPPORTED_SOPTAMP_PARTS = EnumSet.of(
        SoptPart.BACKEND, SoptPart.FRONTEND, SoptPart.PM,
        SoptPart.MARKETER, SoptPart.RESEARCHER, SoptPart.ORGANIZER, SoptPart.CX
    );

    static String toSoptPartName(String partCode, String roleCode, String teamCode) {
        if (roleCode == null) {
            return toBasePartName(partCode);
        }
        return switch (roleCode) {
            case "PRESIDENT" -> SoptPart.PRESIDENT.getPartName();
            case "VICE_PRESIDENT" -> SoptPart.VICE_PRESIDENT.getPartName();
            case "GENERAL_AFFAIRS" -> SoptPart.GENERAL_AFFAIR.getPartName();
            case "ART_DIRECTOR" -> SoptPart.NONE.getPartName();
            case "TEAM_LEADER" -> toTeamLeaderPartName(teamCode);
            case "PART_LEADER" -> toPartLeaderPartName(partCode);
            default -> toBasePartName(partCode);
        };
    }

    private static String toTeamLeaderPartName(String teamCode) {
        if (teamCode == null) {
            return SoptPart.NONE.getPartName();
        }
        return switch (teamCode) {
            case "MAKERS" -> SoptPart.MAKERS_TEAM_LEADER.getPartName();
            case "MEDIA" -> SoptPart.MEDIA_TEAM_LEADER.getPartName();
            case "OPERATION" -> SoptPart.OPERATIONS_TEAM_LEADER.getPartName();
            default -> SoptPart.NONE.getPartName();
        };
    }

    private static String toPartLeaderPartName(String partCode) {
        if (partCode == null) {
            return SoptPart.NONE.getPartName();
        }
        return switch (partCode) {
            case "PLAN" -> SoptPart.PLAN_PART_LEADER.getPartName();
            case "DESIGN" -> SoptPart.DESIGN_PART_LEADER.getPartName();
            case "ANDROID" -> SoptPart.ANDROID_PART_LEADER.getPartName();
            case "IOS" -> SoptPart.IOS_PART_LEADER.getPartName();
            case "WEB" -> SoptPart.WEB_PART_LEADER.getPartName();
            case "SERVER" -> SoptPart.SERVER_PART_LEADER.getPartName();
            default -> toBasePartName(partCode);
        };
    }

    private static String toBasePartName(String partCode) {
        try {
            SoptPart part = SoptPart.valueOf(partCode);
            if (UNSUPPORTED_SOPTAMP_PARTS.contains(part)) {
                return SoptPart.NONE.getPartName();
            }
            return part.getPartName();
        } catch (IllegalArgumentException | NullPointerException e) {
            return SoptPart.NONE.getPartName();
        }
    }
}
