package org.sopt.app.interfaces.postgres.soptletter;

import org.sopt.app.domain.entity.soptletter.SoptLetterTopicRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoptLetterTopicReadRepository extends JpaRepository<SoptLetterTopicRead, Long> {

    @Modifying
    @Query("""
        INSERT INTO SoptLetterTopicRead (userId, topicId, lastReadLetterId, createdAt, updatedAt)
        VALUES (:userId, :topicId, :lastReadLetterId, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        ON CONFLICT (userId, topicId) DO UPDATE
        SET lastReadLetterId = GREATEST(lastReadLetterId, :lastReadLetterId),
            updatedAt = CURRENT_TIMESTAMP
        """)
    int upsertLastReadLetterId(
        @Param("userId") Long userId,
        @Param("topicId") Long topicId,
        @Param("lastReadLetterId") Long lastReadLetterId
    );
}
