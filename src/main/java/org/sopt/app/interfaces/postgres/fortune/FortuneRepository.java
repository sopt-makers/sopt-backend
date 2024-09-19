package org.sopt.app.interfaces.postgres.fortune;

import java.time.LocalDate;
import java.util.Optional;
import org.sopt.app.domain.entity.fortune.Fortune;

public interface FortuneRepository {
    Optional<Fortune> findByRelatedUserIdAndCheckedAt(final Long userId, final LocalDate checkedAt);
}
