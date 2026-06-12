package org.sopt.app.interfaces.postgres.soptletter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.sopt.app.domain.entity.soptletter.SoptLetter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoptLetterRepository extends JpaRepository<SoptLetter, Long> {

    Optional<SoptLetter> findFirstByTopicIdOrderByIdDesc(Long topicId);

    boolean existsByIdAndTopicId(Long id, Long topicId);

    List<SoptLetter> findAllByTopicIdOrderByIdDesc(Long topicId, Pageable pageable);

    List<SoptLetter> findAllByTopicIdAndIdLessThanOrderByIdDesc(Long topicId, Long cursor, Pageable pageable);

    long countByTopicId(Long topicId);

    long countByAuthorProfileIdAndCreatedAtGreaterThanEqual(Long authorProfileId, LocalDateTime startOfDay);

    @Modifying
    @Query("UPDATE SoptLetter l SET l.likeCount = COALESCE(l.likeCount, 0) + 1 WHERE l.id = :soptLetterId")
    int increaseLikeCount(@Param("soptLetterId") Long soptLetterId);

    @Modifying
    @Query("""
        UPDATE SoptLetter l
        SET l.likeCount = CASE
            WHEN l.likeCount IS NULL OR l.likeCount <= 0 THEN 0
            ELSE l.likeCount - 1
        END
        WHERE l.id = :soptLetterId
        """)
    int decreaseLikeCount(@Param("soptLetterId") Long soptLetterId);
}
