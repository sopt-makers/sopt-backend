package org.sopt.app.interfaces.postgres.soptletter;

import java.util.List;
import org.sopt.app.domain.entity.soptletter.SoptLetterTopic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoptLetterTopicRepository extends JpaRepository<SoptLetterTopic, Long> {

    List<SoptLetterTopic> findAllByOrderByCreatedAtDesc();
}
