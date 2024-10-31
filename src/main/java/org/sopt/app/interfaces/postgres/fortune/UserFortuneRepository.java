package org.sopt.app.interfaces.postgres.fortune;

import java.util.Optional;
import org.sopt.app.domain.entity.fortune.UserFortune;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserFortuneRepository extends JpaRepository<UserFortune, Long> {
    Optional<UserFortune> findByUserId(final Long userId);

    @Query("DELETE FROM UserFortune u WHERE u.userId = :userId")
    void deleteAllByUserIdInQuery(@Param("userId") Long userId);
}
