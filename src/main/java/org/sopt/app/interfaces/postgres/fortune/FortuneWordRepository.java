package org.sopt.app.interfaces.postgres.fortune;

import java.time.LocalDate;
import java.util.Optional;
import org.sopt.app.domain.entity.fortune.FortuneWord;

public interface FortuneWordRepository {
    Optional<FortuneWord> findByRelatedUserIdAndCheckedAt(final Long userId, final LocalDate checkedAt);
}
