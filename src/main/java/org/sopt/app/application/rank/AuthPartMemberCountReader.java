package org.sopt.app.application.rank;

import java.util.EnumMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.enums.SoptPart;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthPartMemberCountReader {

    private final JdbcTemplate jdbcTemplate;

    @Value("${makers.auth.schema}")
    private String authSchema;

    public Map<SoptPart, Long> getCurrentGenerationPartMemberCounts(Long generation) {
        String sql = """
            SELECT part, COUNT(*) AS member_count
            FROM %s.user_activity_histories
            WHERE generation = ?
              AND is_sopt = true
              AND role = 'MEMBER'
              AND part IN ('IOS', 'ANDROID', 'DESIGN', 'PLAN', 'SERVER', 'WEB')
            GROUP BY part
            """.formatted(authSchema);

        return jdbcTemplate.query(sql, rs -> {
            Map<SoptPart, Long> result = new EnumMap<>(SoptPart.class);

            while (rs.next()) {
                result.put(
                    SoptPart.valueOf(rs.getString("part")),
                    rs.getLong("member_count")
                );
            }

            return result;
        }, generation);
    }
}
