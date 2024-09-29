package org.sopt.app.interfaces.postgres.fortune;

import java.util.Optional;
import org.sopt.app.domain.entity.fortune.FortuneWord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FortuneWordJpaRepository extends JpaRepository<FortuneWord, Long> {

    Optional<FortuneWord> findById(final Long id);

}
