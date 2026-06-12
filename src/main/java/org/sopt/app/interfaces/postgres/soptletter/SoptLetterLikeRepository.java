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
    @Query("DELETE From SoptLetterLike l WHERE l.letterId = :letterId")
    void deleteAllByLetterIdInQuery(@Param("letterId") Long letterId);
}
