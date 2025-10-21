package org.sopt.app.interfaces.postgres;

import lombok.RequiredArgsConstructor;
import org.intellij.lang.annotations.Language;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class ClapMilestoneGuard {
    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

    @Transactional
    public boolean tryMarkFirstHit(long stampId, int milestone) {
        String sql =
            "INSERT INTO " + schema + ".clap_milestone_hit (stamp_id, milestone, created_at) " +
                "VALUES (?, ?, now()) " +
                "ON CONFLICT (stamp_id, milestone) DO NOTHING";
        return jdbcTemplate.update(sql, stampId, milestone) == 1;
    }
}