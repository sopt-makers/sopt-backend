package org.sopt.app.v1.interfaces.postgres;

import java.util.Optional;
import org.sopt.app.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepositoryV1 extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByNickname(String nickname);

    Optional<User> findUserById(Long userId);
}
