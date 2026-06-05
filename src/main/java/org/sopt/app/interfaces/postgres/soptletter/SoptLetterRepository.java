package org.sopt.app.interfaces.postgres.soptletter;


import java.time.LocalDateTime;
import java.util.Optional;
import org.sopt.app.domain.entity.soptletter.SoptLetter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoptLetterRepository extends JpaRepository<SoptLetter, Long> {

    Optional<SoptLetter> findFirstByTopicIdOrderByIdDesc(Long topicId);

    long countByAuthorProfileIdAndCreatedAtAfter(Long authorProfileId, LocalDateTime startOfDay);
}
