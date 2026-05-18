package org.sopt.app.application.soptamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUserProfileReader {

    private final JdbcTemplate jdbcTemplate;

    @Value("${makers.auth.schema}")
    private String authSchema;

    /**
     * auth DB에서 전체 유저 프로필 조회 (앱잼 시즌용 — OB 포함 전체 대상)
     */
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
                    0, // activityId — 배치 upsert에서 미사용
                    rs.getInt("generation"),
                    AuthSoptPartMapper.toSoptPartName(
                        rs.getString("part"),
                        rs.getString("role"),
                        rs.getString("team")
                    ),
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

    private record AuthUserProfile(
        String name,
        String profileImage,
        String birthday,
        String phone,
        String email
    ) {
    }
}
