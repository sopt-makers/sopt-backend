package org.sopt.app.interfaces.postgres.fortune;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.sopt.app.domain.entity.fortune.FortuneWord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FortuneWordJpaRepository extends JpaRepository<FortuneWord, Long> {
    @NotNull
    Optional<FortuneWord> findById(@NotNull final Long id);
}
