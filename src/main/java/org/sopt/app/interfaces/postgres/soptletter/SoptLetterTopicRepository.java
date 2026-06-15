package org.sopt.app.interfaces.postgres.soptletter;

import java.util.List;
import org.sopt.app.domain.entity.soptletter.SoptLetterTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SoptLetterTopicRepository extends JpaRepository<SoptLetterTopic, Long> {

    List<SoptLetterTopic> findAllByOrderByCreatedAtDesc();

    @Query("SELECT t FROM SoptLetterTopic t WHERE t.isDefault = true ORDER BY t.createdAt DESC")
    List<SoptLetterTopic> findAllDefaultTopicsOrderByCreatedAtDesc();
}
