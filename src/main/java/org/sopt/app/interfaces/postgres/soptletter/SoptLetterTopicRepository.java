package org.sopt.app.interfaces.postgres.soptletter;

import org.sopt.app.domain.entity.soptletter.SoptLetterTopic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoptLetterTopicRepository extends JpaRepository<SoptLetterTopic, Long> {

}
