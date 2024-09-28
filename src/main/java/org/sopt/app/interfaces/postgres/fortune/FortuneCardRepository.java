package org.sopt.app.interfaces.postgres.fortune;

import java.util.List;
import java.util.Optional;
import org.sopt.app.domain.entity.fortune.FortuneCard;

public interface FortuneCardRepository {
    Optional<FortuneCard> findByRelatedUserId(final Long userId);

    List<Long> findAllIds();
}
