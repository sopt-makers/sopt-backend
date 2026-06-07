package org.sopt.app.interfaces.postgres.soptletter;

import org.sopt.app.domain.entity.soptletter.SoptLetterLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoptLetterLikeRepository extends JpaRepository<SoptLetterLike, Long> {
    boolean existsByLetterIdAndUserId(Long letterId, Long userId);
    void deleteByLetterId(Long letterId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE From SoptLetterLike l WHERE l.letterId = :letterId")
    void deleteAllByLetterIdInQuery(@Param("letterId") Long letterId);
}
