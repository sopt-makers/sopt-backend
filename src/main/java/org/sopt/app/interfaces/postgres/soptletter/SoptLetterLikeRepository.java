package org.sopt.app.interfaces.postgres.soptletter;

import java.util.Set;
import org.sopt.app.domain.entity.soptletter.SoptLetterLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoptLetterLikeRepository extends JpaRepository<SoptLetterLike, Long> {
    boolean existsByLetterIdAndUserId(Long letterId, Long userId);

    @Query("SELECT l.letterId FROM SoptLetterLike l WHERE l.userId = :userId AND l.letterId IN :letterIds")
    Set<Long> findLikedLetterIdsByUserIdAndLetterIdIn(@Param("userId") Long userId, @Param("letterIds") Set<Long> letterIds);

    @Modifying
    @Query(
        value = """
            INSERT INTO ${spring.jpa.properties.hibernate.default_schema}.sopt_letter_like (user_id, letter_id, created_at, updated_at)
            VALUES (:userId, :letterId, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            ON CONFLICT (letter_id, user_id) DO NOTHING
            """,
        nativeQuery = true
    )
    int insertIgnore(@Param("userId") Long userId, @Param("letterId") Long letterId);

    @Modifying
    @Query("DELETE FROM SoptLetterLike l WHERE l.letterId = :letterId AND l.userId = :userId")
    int deleteByLetterIdAndUserId(@Param("letterId") Long letterId, @Param("userId") Long userId);

    @Modifying
    @Query("DELETE From SoptLetterLike l WHERE l.letterId = :letterId")
    void deleteAllByLetterIdInQuery(@Param("letterId") Long letterId);
}
