package org.sopt.app.interfaces.postgres.fortune;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.sopt.app.domain.entity.fortune.FortuneCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FortuneCardRepository extends JpaRepository<FortuneCard, Long> {
    @NotNull
    Optional<FortuneCard> findById(@NotNull final Long fortuneId);

}
