package org.sopt.app.interfaces.postgres;

import org.sopt.app.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByNickname(String nickname);

    Optional<User> findUserById(Long userId);
}
