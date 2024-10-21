package org.sopt.app.interfaces.postgres.fortune;

import java.util.Optional;
import org.sopt.app.domain.entity.fortune.UserFortune;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFortuneRepository extends JpaRepository<UserFortune, Long> {
    Optional<UserFortune> findByUserId(final Long userId);
}
