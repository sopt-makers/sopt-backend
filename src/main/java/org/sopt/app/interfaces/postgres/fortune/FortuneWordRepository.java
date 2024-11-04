package org.sopt.app.interfaces.postgres.fortune;

import java.util.List;
import java.util.Optional;
import org.sopt.app.domain.entity.fortune.FortuneWord;

public interface FortuneWordRepository {
    List<Long> findAllIds();
    Optional<FortuneWord> findById(final Long id);
}
