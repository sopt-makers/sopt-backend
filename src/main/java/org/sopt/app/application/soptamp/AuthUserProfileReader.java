package org.sopt.app.application.soptamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.sopt.app.domain.enums.SoptPart;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUserProfileReader {

    private final JdbcTemplate jdbcTemplate;

    @Value("${makers.auth.schema}")
    private String authSchema;

    public Map<Long, PlatformUserInfoResponse> getAllUserProfiles() {
        String sql = """
                SELECT u.id AS user_id,
                       u.name,
                       u.profile_image,
                       u.birthday,
                       u.phone,
                       u.email,
                       uah.generation,
                       uah.part,
                       uah.role,
                       uah.is_sopt,
                       uah.team
                FROM %s.users u
                JOIN %s.user_activity_histories uah ON uah.user_id = u.id
                ORDER BY u.id, uah.generation, uah.is_sopt DESC
                """.formatted(authSchema, authSchema);

        Map<Long, AuthUserProfile> profileMap = new HashMap<>();
        Map<Long, List<PlatformUserInfoResponse.SoptActivities>> activitiesMap = new HashMap<>();

        jdbcTemplate.query(sql, rs -> {
            long userId = rs.getLong("user_id");
            profileMap.putIfAbsent(userId, new AuthUserProfile(
                rs.getString("name"),
                rs.getString("profile_image"),
                rs.getString("birthday"),
                rs.getString("phone"),
                rs.getString("email")
            ));
            activitiesMap.computeIfAbsent(userId, k -> new ArrayList<>())
                .add(new PlatformUserInfoResponse.SoptActivities(
                    0,
                    rs.getInt("generation"),
                    toSoptPartName(rs.getString("part"), rs.getString("role"), rs.getString("team")),
                    rs.getString("team"),
                    rs.getBoolean("is_sopt")
                ));
        });

        return activitiesMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> buildProfile(e.getKey(), profileMap.get(e.getKey()), e.getValue())
            ));
    }

    private PlatformUserInfoResponse buildProfile(long userId, AuthUserProfile profile,
            List<PlatformUserInfoResponse.SoptActivities> activities) {
        int lastGeneration = activities.stream()
            .mapToInt(PlatformUserInfoResponse.SoptActivities::generation)
            .max()
            .orElse(0);
        return new PlatformUserInfoResponse(
            (int) userId,
            profile.name(),
            profile.profileImage(),
            profile.birthday(),
            profile.phone(),
            profile.email(),
            lastGeneration,
            activities
        );
    }

    private String toSoptPartName(String partCode, String roleCode, String teamCode) {
        if (roleCode == null) {
            return toBasePartName(partCode);
        }
        return switch (roleCode) {
            case "PRESIDENT" -> SoptPart.PRESIDENT.getPartName();
            case "VICE_PRESIDENT" -> SoptPart.VICE_PRESIDENT.getPartName();
            case "GENERAL_AFFAIRS" -> SoptPart.GENERAL_AFFAIR.getPartName();
            case "ART_DIRECTOR" -> SoptPart.ART_DIRECTOR.getPartName();
            case "TEAM_LEADER" -> toTeamLeaderPartName(teamCode);
            case "PART_LEADER" -> toPartLeaderPartName(partCode);
            default -> toBasePartName(partCode);
        };
    }

    private String toTeamLeaderPartName(String teamCode) {
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

    private String toPartLeaderPartName(String partCode) {
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

    private String toBasePartName(String partCode) {
        try {
            return SoptPart.valueOf(partCode).getPartName();
        } catch (IllegalArgumentException | NullPointerException e) {
            return SoptPart.NONE.getPartName();
        }
    }

    private record AuthUserProfile(
        String name,
        String profileImage,
        String birthday,
        String phone,
        String email
    ) {
    }
}
