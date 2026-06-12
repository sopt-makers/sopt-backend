package org.sopt.app.interfaces.postgres.soptletter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.sopt.app.domain.entity.soptletter.SoptLetter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoptLetterRepository extends JpaRepository<SoptLetter, Long> {

    Optional<SoptLetter> findFirstByTopicIdOrderByIdDesc(Long topicId);

    List<SoptLetter> findAllByTopicIdOrderByIdDesc(Long topicId, Pageable pageable);

    List<SoptLetter> findAllByTopicIdAndIdLessThanOrderByIdDesc(Long topicId, Long cursor, Pageable pageable);

    long countByTopicId(Long topicId);

    long countByAuthorProfileIdAndCreatedAtGreaterThanEqual(Long authorProfileId, LocalDateTime startOfDay);
}
