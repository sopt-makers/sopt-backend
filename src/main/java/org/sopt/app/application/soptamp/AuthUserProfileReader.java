package org.sopt.app.application.soptamp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    public List<PlatformUserInfoResponse> getAllUserProfiles() {
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
                """.formatted(authSchema, authSchema);

        Map<Long, AuthUserProfile> profileMap = new HashMap<>();
        Map<Long, List<PlatformUserInfoResponse.SoptActivities>> activitiesMap = new LinkedHashMap<>();

        jdbcTemplate.query(sql, rs -> {
            long userId = rs.getLong("user_id");
            if (!profileMap.containsKey(userId)) {
                profileMap.put(userId, extractProfile(rs));
            }
            activitiesMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(extractActivity(rs));
        });

        return activitiesMap.entrySet().stream()
            .map(e -> buildProfile(e.getKey(), profileMap.get(e.getKey()), e.getValue()))
            .toList();
    }

    private AuthUserProfile extractProfile(ResultSet rs) throws SQLException {
        return new AuthUserProfile(
            rs.getString("name"),
            rs.getString("profile_image"),
            rs.getString("birthday"),
            rs.getString("phone"),
            rs.getString("email")
        );
    }

    private PlatformUserInfoResponse.SoptActivities extractActivity(ResultSet rs) throws SQLException {
        return new PlatformUserInfoResponse.SoptActivities(
            0,
            rs.getInt("generation"),
            AuthSoptPartMapper.toSoptPartName(
                rs.getString("part"),
                rs.getString("role"),
                rs.getString("team")
            ),
            rs.getString("team"),
            rs.getBoolean("is_sopt")
        );
    }

    private PlatformUserInfoResponse buildProfile(long userId, AuthUserProfile profile,
            List<PlatformUserInfoResponse.SoptActivities> activities) {
        int lastGeneration = activities.stream()
            .filter(a -> Boolean.TRUE.equals(a.isSopt()))
            .mapToInt(PlatformUserInfoResponse.SoptActivities::generation)
            .max()
            .orElseGet(() -> activities.stream()
                .mapToInt(PlatformUserInfoResponse.SoptActivities::generation)
                .max()
                .orElse(0));
        return new PlatformUserInfoResponse(
            Math.toIntExact(userId),
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
