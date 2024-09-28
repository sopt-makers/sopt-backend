package org.sopt.app.interfaces.postgres.fortune;

import java.time.LocalDate;
import java.util.Optional;
import org.sopt.app.domain.entity.fortune.FortuneWord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FortuneWordRepository extends JpaRepository<FortuneWord, Long> {
    Optional<FortuneWord> findByUserIdAndCheckAt(final Long userId, final LocalDate checkedAt);
}
