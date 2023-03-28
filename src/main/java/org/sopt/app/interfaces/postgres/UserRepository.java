package org.sopt.app.interfaces.postgres;

import java.util.Optional;
import org.sopt.app.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByNickname(String nickname);

    Optional<User> findUserById(Long userId);

    Optional<User> findUserByPlaygroundId(Long playgroundId);
}
