package org.sopt.app.interfaces.postgres.soptletter;

import org.sopt.app.domain.entity.soptletter.SoptLetterLikeNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface SoptLetterLikeNotificationRepository extends JpaRepository<SoptLetterLikeNotification, Long> {

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = """
        INSERT INTO {h-schema}sopt_letter_like_notification (user_id, letter_id, created_at, updated_at)
        VALUES (:userId, :letterId, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        ON CONFLICT (letter_id, user_id) DO NOTHING
        """, nativeQuery = true)
    int insertIgnore(@Param("userId") Long userId, @Param("letterId") Long letterId);
}
