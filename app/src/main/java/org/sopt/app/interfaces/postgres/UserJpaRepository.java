package org.sopt.app.interfaces.postgres;

import org.sopt.app.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
}
