package org.sopt.app.interfaces.postgres;

import lombok.RequiredArgsConstructor;
import org.intellij.lang.annotations.Language;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class ClapMilestoneGuard {
    private final JdbcTemplate jdbcTemplate;

    @Language("PostgreSQL")
    private static final String SQL = """
        
            INSERT INTO clap_milestone_hit (stamp_id, milestone, created_at)
        VALUES (?, ?, now())
        ON CONFLICT (stamp_id, milestone) DO NOTHING
        """;

    @Transactional
    public boolean tryMarkFirstHit(long stampId, int milestone) {
        return jdbcTemplate.update(SQL, stampId, milestone) == 1;
    }
}