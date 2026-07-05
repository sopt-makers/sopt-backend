package org.sopt.app.interfaces.postgres.soptletter;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.app.domain.entity.soptletter.SoptLetterTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoptLetterTopicRepository extends JpaRepository<SoptLetterTopic, Long> {

    List<SoptLetterTopic> findAllByOrderByCreatedAtDesc();

    @Query("SELECT t FROM SoptLetterTopic t WHERE t.isDefault = true ORDER BY t.createdAt DESC")
    List<SoptLetterTopic> findAllDefaultTopicsOrderByCreatedAtDesc();

    @Query("SELECT t FROM SoptLetterTopic t WHERE t.isDefault = false ORDER BY t.createdAt DESC")
    List<SoptLetterTopic> findAllNormalTopicsOrderByCreatedAtDesc();

    @Query("""
        SELECT t FROM SoptLetterTopic t
        WHERE t.ctaText IS NOT NULL
            AND t.startedAt <= :now
            AND t.endedAt >= :now
        ORDER BY t.startedAt DESC, t.id DESC
        """)
    List<SoptLetterTopic> findActiveCtas(@Param("now") LocalDateTime now);
}
