package org.sopt.app.interfaces.postgres;

import java.util.List;
import java.util.Optional;
import org.sopt.app.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserById(Long userId);

    Optional<User> findUserByPlaygroundId(Long playgroundId);

    List<User> findAllByPlaygroundIdIn(List<Long> playgroundIds);

    @Query("SELECT DISTINCT u.playgroundId FROM User u")
    List<Long> findAllPlaygroundId();

    @Query("SELECT u.id FROM User u WHERE u.playgroundId = :playgroundId")
    Long findIdByPlaygroundId(@Param("playgroundId") Long playgroundId);
}
